package io.micronaut.views.react;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.scheduling.io.watch.event.FileChangedEvent;
import io.micronaut.scheduling.io.watch.event.WatchEventType;
import io.micronaut.views.react.util.BeanPool;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Loads source code for the scripts, reloads them on file change and manages the {@link BeanPool context pool}.
 */
@Factory
class ContextPoolManager implements ApplicationEventListener<FileChangedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(ContextPoolManager.class);
    private final ResourceResolver resourceResolver;
    private final ReactViewsRendererConfiguration reactViewsRendererConfiguration;
    private final ApplicationContext applicationContext;
    // We cache the Source objects because they are expensive to create, but, we don't want them
    // to be singleton beans so we can recreate them on file change.
    private Source serverBundle;  // L(this)
    private Source renderScript;  // L(this)
    private BeanPool<ReactJSContext> pool;  // L(this)

    public ContextPoolManager(ResourceResolver resourceResolver, ReactViewsRendererConfiguration reactViewsRendererConfiguration, ApplicationContext applicationContext) {
        this.resourceResolver = resourceResolver;
        this.reactViewsRendererConfiguration = reactViewsRendererConfiguration;
        this.applicationContext = applicationContext;
    }

    @Singleton
    public synchronized BeanPool<ReactJSContext> contextPool() {
        if (pool != null) {
            pool.clear();
        }
        pool = new BeanPool<>(this::createContext);
        return pool;
    }

    private ReactJSContext createContext() {
        try {
            return applicationContext.createBean(ReactJSContext.class, serverBundle(), renderScript());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized Source serverBundle() throws IOException {
        if (serverBundle == null) {
            serverBundle = loadSource(resourceResolver, reactViewsRendererConfiguration.getServerBundlePath(), ".server-bundle-path");
        }
        return serverBundle;
    }

    private synchronized Source renderScript() throws IOException {
        if (renderScript == null) {
            renderScript = loadSource(resourceResolver, reactViewsRendererConfiguration.getRenderScript(), ".render-script");
        }
        return renderScript;
    }

    private static Source loadSource(ResourceResolver resolver, String desiredPath, String propName) throws IOException {
        Optional<URL> sourceURL = resolver.getResource(desiredPath);
        if (sourceURL.isEmpty()) {
            throw new FileNotFoundException(format("Javascript %s could not be found. Check your %s property.", desiredPath, ReactViewsRendererConfiguration.PREFIX + propName));
        }
        URL url = sourceURL.get();
        try (var reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)) {
            String path = url.getPath();
            var fileName = path.substring(path.lastIndexOf('/') + 1);
            Source.Builder sourceBuilder = Source.newBuilder("js", reader, fileName);
            return sourceBuilder.mimeType("application/javascript+module").build();
        }
    }

    @Override
    public synchronized void onApplicationEvent(FileChangedEvent event) {
        if (event.getEventType() == WatchEventType.DELETE) {
            return;
        }

        var path = event.getPath().toAbsolutePath();
        if (path.equals(Paths.get(serverBundle.getPath()).toAbsolutePath())) {
            serverBundle = null;
        }
        if (path.equals(Paths.get(renderScript.getPath()).toAbsolutePath())) {
            renderScript = null;
        }

        if (serverBundle != null && renderScript != null) {
            return;
        }

        // Clearing the pool ensures that new requests go via the pool and from there, back to
        // createContext() which will in turn then reload the files on disk.
        pool.clear();
        LOG.info("Reloaded React SSR bundle due to file change.");
    }
}
