package io.micronaut.views.react;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static io.micronaut.http.HttpHeaders.*;
import static java.lang.String.format;

/**
 * Instantiates GraalJS and uses it to render React components server side. The view name is the (exported) name of
 * the React component (class or function).
 */
@Singleton
public class ReactViewsRenderer<PROPS, REQUEST> implements ViewsRenderer<PROPS, REQUEST>, AutoCloseable {
    /**
     * TO DO
     *
     * - Find a way to use `renderToPipeableStream`?
     * - Allow the fetch API to use the Micronaut http client API?
     * - Get rid of the JSON serialization across the border in favour of nested proxy object mappings.
     * - Work out how to make <Suspense> work.
     * - Figure out the debugging story.
     * - Implement the TextEncoder/Decoder objects in GraalJS.
     * - Maybe update the `micronaut-spa-app` sample.
     */

    private static final Logger LOG = LoggerFactory.getLogger(ReactViewsRenderer.class);

    /**
     * The name of the bundled/webpacked JS file that must be placed in the view folder.
     */
    public static final String MJS_FILENAME = "ssr-components.mjs";

    // The context is a JavaScript world (global object and everything reachable from it).
    private Context js;

    // Our top-level function that returns what's needed.
    private Value renderJSFun;

    @Client
    private final HttpClient httpClient;

    @Language("js")
    private static final String RENDER_SRC = """
    (async function micronautRender(component, props, callback) {
        globalThis.__micronaut_prefetch = callback.recordPrefetch;
        const element = React.createElement(component, props, null);

        var stream;
        do {
            var prefetchedData = callback.getPrefetchedData();

            // Data to be passed to the browser after the main HTML has finished loading.
            const boot = {
                rootProps: props,
                rootComponent: component.name,
                prefetch: prefetchedData
            };

            var bootstrapScriptContent = `var __micronaut_boot = ${JSON.stringify(boot)};`;
            stream = await ReactDOMServer.renderToReadableStream(element, {
                bootstrapScriptContent: bootstrapScriptContent,
                bootstrapScripts: ["/static/client.js"]
            });
        } while (callback.didPrefetch());

        // This ugliness is because renderToPipeableStream (what we should really use) is only in the node build
        // of react-dom/server, but we use the browser build. Trying to use the node build causes various errors
        // and problems that I don't yet understand, something to do with module formats.
        const reader = stream.getReader();
        while (true) {
          const { done, value } = await reader.read();
          if (done) break;
          callback.write(value);
        }
    })
    """;

    // This is the overall module that exports both the React utilities we need, and the user's actual components.
    // It is expected to be produced with webpack.
    private Value ssrModule;

    private final String folder;
    private FileTime lastModified;
    private Path bundlePath;

    private static final Handler LOG_HANDLER = new JSLogHandler();

    private static final List<String> REQUIRED_EXPORTED_SYMBOLS = List.of("React", "ReactDOMServer");

    /**
     * Construct this renderer. Don't call it yourself, as Micronaut Views will set it up for you.
     *
     * @param httpClient An injected Micronaut HTTP client used for pre-fetching requests made with
     *                   the SWR Javascript library.
     * @param viewsConfiguration Where to find the server-side Javascript.
     */
    @Inject
    public ReactViewsRenderer(ViewsConfiguration viewsConfiguration, HttpClient httpClient) {
        folder = viewsConfiguration.getFolder();
        this.httpClient = httpClient;
    }

    @PostConstruct
    void init() throws IOException, IllegalStateException {
        // We start in 'target' when run from Maven.
        bundlePath = Path.of(folder).resolve(MJS_FILENAME).toAbsolutePath().normalize();
        if (!Files.exists(bundlePath))
            throw new FileNotFoundException(bundlePath + " was not found in workspace, try running `npm run build` in the js directory.");

        lastModified = Files.getLastModifiedTime(bundlePath);

        LOG.info("Initializing React SSR with {}", bundlePath);
        Logger jsLogger = LoggerFactory.getLogger("js");
        js = Context.newBuilder("js")
            .allowExperimentalOptions(true)
            .logHandler(LOG_HANDLER)
            .allowAllAccess(true)  // TODO: Sandbox
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw")
            .out(new OutputStreamToSLF4J(jsLogger, Level.INFO))
            .err(new OutputStreamToSLF4J(jsLogger, Level.ERROR))
            .build();

        Value global = js.getBindings("js");

        try (var reader = Files.newBufferedReader(bundlePath)) {
            Source source = Source.newBuilder("js", reader, MJS_FILENAME)
                .mimeType("application/javascript+module")
                .build();
            ssrModule = js.eval(source);

            // Make sure we can eval some code that uses the required modules.
            for (var name : REQUIRED_EXPORTED_SYMBOLS) {
                if (!ssrModule.hasMember(name))
                    throw new IllegalStateException(format("Your %s bundle must re-export the %s module.", MJS_FILENAME, name));
                global.putMember(name, ssrModule.getMember(name));
            }

            renderJSFun = js.eval(Source.newBuilder("js", RENDER_SRC.stripIndent(), "render.js").build());
        }
    }

    /**
     * Given a &lt;ViewName/&gt; and optionally an object that represents some props (can be a map
     * or introspectable object), returns hydratable HTML that can be booted on the client using
     * the React libraries.
     *
     * @param viewName The function or class name of the React component to use as the root. It should return an html root tag.
     * @param props If non-null, will be exposed to the given component as React props.
     * @param request The HTTP request object, used for propagation of headers and cookies to prefetched HTTP requests.
     */
    @Override
    public @NonNull Writable render(@NonNull String viewName, @Nullable PROPS props, @Nullable REQUEST request) {
        maybeReInit();
        return writer -> render(viewName, props, writer);
    }

    @Override
    public synchronized boolean exists(@NonNull String viewName) {
        if (REQUIRED_EXPORTED_SYMBOLS.contains(viewName))
            return false;
        return ssrModule.hasMember(viewName);
    }

    /** @hidden  */
    public class RenderCallback {
        private final Set<URI> urlsToPrefetch = new HashSet<>();

        private final HashMap<URI, byte[]> prefetchedData = new HashMap<>();

        private final Writer responseWriter;
        private final String componentName;

        public RenderCallback(Writer responseWriter, String componentName) {
            this.responseWriter = responseWriter;
            this.componentName = componentName;
        }

        @HostAccess.Export
        public Value recordPrefetch(String urlAsString) throws URISyntaxException {
            URI url = new URI(urlAsString);

            // If we already fetched it in a previous round, return the data now.
            var alreadyFetched = getPrefetchedDataForURL(url);
            if (alreadyFetched != null)
                return alreadyFetched;

            // Otherwise, queue it for fetching.
            if (urlsToPrefetch.add(url))
                LOG.debug("React SSR request for <" + componentName + "/> attempted to fetch " + url);

            return null;
        }

        @org.jetbrains.annotations.Nullable
        private Value getPrefetchedDataForURL(URI url) {
            var result = prefetchedData.get(url);
            if (result == null) return null;
            var jsonParse = js.eval("js", "JSON.parse");
            // TODO: Work out the story around result parsing in the absence of the fetcher?
            return jsonParse.execute(new String(result, StandardCharsets.UTF_8));
        }

        @HostAccess.Export
        public ProxyObject getPrefetchedData() {
            // This is called at the start of each render cycle. We'll drain the queue and return the cache built up
            // so far, which will be serialized to JSON and returned to the client. If the next render completes
            // without any new URLs being queued, the result will be returned as-is, otherwise we'll be called again
            // to do more fetching.
            var workList = new ArrayList<>(urlsToPrefetch);
            urlsToPrefetch.clear();
            for (URI url : workList) {
                UriBuilder builder = UriBuilder.of(url);
                if (url.getHost() == null) {
                    // A URL like '/foo/bar' is valid. TODO: dynamic port discovery?
                    builder = builder.host("localhost").port(8080).scheme("http");
                }
                var req = HttpRequest.GET(builder.build())
                    .header(CONTENT_ENCODING, "UTF-8")
                    .header(ACCEPT, "application/vnd.github.v3+json, application/json")
                    .header(USER_AGENT, "Micronaut React SSR Prerender");
                var response = httpClient.toBlocking().retrieve(req);
                prefetchedData.put(url, response.getBytes(StandardCharsets.UTF_8));
            }

            var resultMap = new HashMap<String, Object>();
            for (var entry : prefetchedData.entrySet()) {
                resultMap.put(entry.getKey().toASCIIString(), getPrefetchedDataForURL(entry.getKey()));
            }

            return ProxyObject.fromMap(resultMap);
        }

        @HostAccess.Export
        public void write(int[] unsignedBytes) {
            try {
                byte[] bytes = new byte[unsignedBytes.length];
                for (int i = 0; i < unsignedBytes.length; i++)
                    bytes[i] = (byte) unsignedBytes[i];
                responseWriter.write(new String(bytes, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @HostAccess.Export
        public boolean didPrefetch() {
            return !urlsToPrefetch.isEmpty();
        }
    }

    private synchronized void render(String componentName, PROPS props, Writer writer) {
        Value component = ssrModule.getMember(componentName);
        if (component == null)
            throw new IllegalArgumentException("Component name %s wasn't exported from the SSR module.".formatted(componentName));

        var propsProxy = new IntrospectableToPolyglotObject<>(js, true, props);
        var renderCallback = new RenderCallback(writer, componentName);
        renderJSFun.execute(component, propsProxy, renderCallback);
    }

    @PreDestroy
    @Override
    public synchronized void close() {
        js.close();
    }

    /**
     * Check the bundle file on disk to see if it's been recompiled.
     */
    private void maybeReInit() {
        try {
            FileTime time = Files.getLastModifiedTime(bundlePath);
            if (time.compareTo(lastModified) > 0) {
                close();
                init();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class JSLogHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            String message = record.getMessage();
            Throwable thrown = record.getThrown();
            String level = record.getLevel().getName();
            switch (level) {
                case "SEVERE":
                    LOG.error(message, thrown);
                    break;
                case "WARNING":
                    LOG.warn(message, thrown);
                    break;
                case "INFO":
                    LOG.info(message, thrown);
                    break;
                case "CONFIG":
                case "FINE":
                    LOG.debug(message, thrown);
                    break;
                case "FINER":
                case "FINEST":
                    LOG.trace(message, thrown);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + level);
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }
}
