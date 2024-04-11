package io.micronaut.views.react;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

class JSContext implements AutoCloseable {
    @Inject
    JSEngineLogHandler engineLogHandler;

    @Inject
    JSBundlePaths jsBundlePaths;

    @Inject
    CompiledJS compiledJS;

    @Inject
    ReactViewsRendererConfiguration configuration;

    Context polyglotContext;
    Value render;

    // Symbols the user's server side bundle might supply us with.
    private static final List<String> IMPORT_SYMBOLS = List.of("React", "ReactDOMServer", "renderToString", "h");

    Value ssrModule;

    @PostConstruct
    void init() throws IOException {
        polyglotContext = createContext();

        Value global = polyglotContext.getBindings("js");
        ssrModule = polyglotContext.eval(compiledJS.source);

//        if (!ssrModule.hasMember("React") && !ssrModule.hasMember("h"))
//            throw new IllegalStateException(format("Your %s bundle must re-export the React module or the 'h' symbol from Preact.", jsBundlePaths.bundleFileName));
//
        // Take all the exports from the components bundle, and expose them to the render script.
        for (var name : ssrModule.getMemberKeys()) {
            global.putMember(name, ssrModule.getMember(name));
        }

        // Evaluate our JS-side framework specific render logic.
        Value renderModule = polyglotContext.eval(loadRenderSource());
        render = renderModule.getMember("ssr");
        if (render == null)
            throw new IllegalArgumentException("Unable to look up ssr function in render script `%s`. Please make sure it is exported.".formatted(configuration.getRenderScript()));
    }

    private Source loadRenderSource() throws IOException {
        String renderScriptName = configuration.getRenderScript();
        String fileName;
        String source;

        if (renderScriptName.startsWith("classpath:")) {
            var resourcePath = renderScriptName.substring("classpath:".length());
            // Even on Windows, classpath specs use /
            fileName = fileNameFromUNIXPath(resourcePath);
            try (var stream = getClass().getResourceAsStream(resourcePath)) {
                if (stream == null)
                    throw new IllegalArgumentException("Render script not found on classpath: " + resourcePath);
                source = new String(stream.readAllBytes(), UTF_8);
            }
        } else if (renderScriptName.startsWith("file:")) {
            var path = Path.of(renderScriptName.substring("file:".length()));
            if (!Files.exists(path))
                throw new IllegalArgumentException("Render script not found: " + renderScriptName);
            fileName = path.normalize().toAbsolutePath().getFileName().toString();
            try (var stream = Files.newInputStream(path)) {
                source = new String(stream.readAllBytes(), UTF_8);
            }
        } else {
            throw new IllegalArgumentException("The renderScript name '%s' must begin with either `classpath:` or `file:`".formatted(renderScriptName));
        }

        return Source.newBuilder("js", source, fileName)
            .mimeType("application/javascript+module")
            .build();
    }

    private static @NotNull String fileNameFromUNIXPath(String resourcePath) {
        String fileName;
        var i = resourcePath.lastIndexOf('/');
        fileName = resourcePath.substring(i + 1);
        return fileName;
    }

    private Context createContext() {
        Logger jsLogger = LoggerFactory.getLogger("js");

        // TODO: Sandboxing is currently incompatible with esm-eval-returns-exports.
        //       If the problem won't be fixed soon, rework to avoid depending on that feature so
        //       the sandbox can be enabled.

        Context.Builder contextBuilder = Context.newBuilder()
            .engine(compiledJS.engine)
            .allowExperimentalOptions(true)
            .logHandler(engineLogHandler)
            .allowAllAccess(true)
            // .sandbox(SandboxPolicy.CONSTRAINED)
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw")
            .out(new OutputStreamToSLF4J(jsLogger, Level.INFO))
            .err(new OutputStreamToSLF4J(jsLogger, Level.ERROR));

        try {
            return contextBuilder.build();
        } catch (ExceptionInInitializerError e) {
            // The catch handler is to work around a bug in Polyglot 24.0.0
            if (e.getCause().getMessage().contains("version compatibility check failed")) {
                throw new IllegalStateException("GraalJS version mismatch or it's missing. Please ensure you have added either org.graalvm.polyglot:js or org.graalvm.polyglot:js-community to your dependencies alongside Micronaut Views React, as it's up to you to select the best engine given your licensing constraints. See the user guide for more detail.");
            } else throw e;
        }
    }

    boolean moduleHasMember(String memberName) {
        assert !IMPORT_SYMBOLS.contains(memberName) : "Should not query the server-side bundle for member name " + memberName;
        return ssrModule.hasMember(memberName);
    }


    @PreDestroy
    @Override
    public synchronized void close() {
        polyglotContext.close();
    }

    public void reinit() throws IOException {
        close();
        init();
    }
}
