/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.react;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.context.watch.BeanWatch;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.scheduling.io.watch.FileChange;
import io.micronaut.scheduling.io.watch.FileChangeBatch;
import io.micronaut.scheduling.io.watch.FileWatcherRegistration;
import io.micronaut.scheduling.io.watch.event.FileChangedEvent;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Source;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The scripts of development mode: besides a {@link FileChangedEvent}, a change is learnt through the watches of
 * {@link ReactJSSourcesWatch}: the process's file watcher, with which the directory of each script read from a file
 * is registered, and the resource watch of the development context, for a script under one of the launcher's
 * resource roots. A change reported more than once reloads once. The production {@link ReactJSSources} reads the
 * scripts once, and drops them only on a {@link FileChangedEvent}.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
@Replaces(ReactJSSources.class)
final class DevelopmentReactJSSources extends ReactJSSources {
    private static final Logger LOG = LoggerFactory.getLogger(DevelopmentReactJSSources.class);
    private static final Duration STABILITY_POLL = Duration.ofMillis(100);
    private static final Duration STABILITY_DEADLINE = Duration.ofSeconds(2);

    private final ResourceResolver resourceResolver;
    private final ReactViewsRendererConfiguration configuration;
    private final ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher;
    private final ReactJSSourcesWatch watch;
    private final Map<Path, FileWatcherRegistration> registrations = new HashMap<>();  // L(this)
    private final List<BeanWatch> resourceWatches = new ArrayList<>();

    // the scripts as last read, to learn when the base class read them again
    private @Nullable Source readServerBundle;  // L(this)
    private @Nullable Source readRenderScript;  // L(this)
    // the files the scripts were read from, kept while a script is dropped
    private @Nullable Path serverBundleOrigin;  // L(this)
    private @Nullable Path renderScriptOrigin;  // L(this)
    // the digest of each file as last read or, while its script is dropped, as last reported: a change reported for a
    // file that still holds it, reported again by another watch, drops nothing
    private @Nullable String serverBundleStamp;  // L(this)
    private @Nullable String renderScriptStamp;  // L(this)
    // reading the scripts again after a change failed: the next write to their files is a change, whatever it holds
    private boolean reloadFailed;  // L(this)

    /**
     * @param resourceResolver The resource resolver
     * @param configuration The configuration
     * @param sourcesChangedEventPublisher The publisher of the reloads
     * @param watch The watches of the scripts' files
     */
    DevelopmentReactJSSources(ResourceResolver resourceResolver,
                              ReactViewsRendererConfiguration configuration,
                              ApplicationEventPublisher<ReactJSSourcesChangedEvent> sourcesChangedEventPublisher,
                              ReactJSSourcesWatch watch) {
        super(resourceResolver, configuration, sourcesChangedEventPublisher);
        this.resourceResolver = resourceResolver;
        this.configuration = configuration;
        this.sourcesChangedEventPublisher = sourcesChangedEventPublisher;
        this.watch = watch;
        resourceWatches.addAll(watch.watchResources(this::resourcesChanged));
    }

    @Override
    synchronized Source serverBundle() {
        Source source = super.serverBundle();
        if (source != readServerBundle) {
            readServerBundle = source;
            serverBundleOrigin = originOf(source);
            serverBundleStamp = stampOf(source);
            watch(serverBundleOrigin);
        }
        return source;
    }

    @Override
    synchronized Source renderScript() {
        Source source = super.renderScript();
        if (source != readRenderScript) {
            readRenderScript = source;
            renderScriptOrigin = originOf(source);
            renderScriptStamp = stampOf(source);
            watch(renderScriptOrigin);
        }
        return source;
    }

    /**
     * Loads the scripts a change dropped, once their files stopped being written, so that a browser refreshed
     * afterwards is rendered with them.
     *
     * @param changeGeneration The generation the change produced
     * @return Whether the scripts of that generation are loaded; false when a later change dropped them again, and
     * will be loaded in turn
     */
    boolean loadChanged(long changeGeneration) {
        awaitStable(configuration.getServerBundlePath());
        awaitStable(configuration.getRenderScript());
        synchronized (this) {
            if (changeGeneration != generation) {
                return false;
            }
            try {
                serverBundle();
                renderScript();
                reloadFailed = false;
            } catch (RuntimeException e) {
                // the render reports it; the browser is refreshed to show it, and the next write is reloaded too
                LOG.warn("Could not load the rebuilt React SSR bundle: {}", e.getMessage());
                reloadFailed = true;
            }
            return true;
        }
    }

    /**
     * A change, a deletion too: a bundler that deletes its output before it writes the next one, and fails to, leaves
     * no script to serve, which rendering reports.
     *
     * @param event The event
     */
    @Override
    public void onApplicationEvent(FileChangedEvent event) {
        changed(List.of(event.getPath()));
    }

    /**
     * Drops the scripts read from any of the changed files, and tells the listeners once if one was dropped. A file a
     * script was read from stays its origin while the script is dropped, so that a later write before it is read
     * again is a change too, and listeners that load the scripts of an earlier change learn that it was superseded.
     *
     * @param paths The changed files
     */
    private void changed(List<Path> paths) {
        long reloaded;
        synchronized (this) {
            boolean dropped = false;
            for (Path changed : paths) {
                Path path = changed.toAbsolutePath().normalize();
                boolean serverBundleChanged = path.equals(serverBundleOrigin);
                boolean renderScriptChanged = path.equals(renderScriptOrigin);
                if (!serverBundleChanged && !renderScriptChanged) {
                    continue;
                }
                String stamp = stampOf(path);
                if (serverBundleChanged && (reloadFailed || !Objects.equals(serverBundleStamp, stamp))) {
                    serverBundle = null;
                    serverBundleStamp = stamp;
                    dropped = true;
                }
                if (renderScriptChanged && (reloadFailed || !Objects.equals(renderScriptStamp, stamp))) {
                    renderScript = null;
                    renderScriptStamp = stamp;
                    dropped = true;
                }
            }
            // a script that was not read yet will be read as it is now; a write already reported through another
            // watch is not a change twice
            if (!dropped) {
                return;
            }
            reloadFailed = false;
            generation++;
            reloaded = generation;
        }
        LOG.info("Reloaded React SSR bundle due to file change.");
        sourcesChangedEventPublisher.publishEvent(new ReactJSSourcesChangedEvent(this, reloaded));
    }

    private void filesChanged(FileChangeBatch batch) {
        List<Path> paths = new ArrayList<>(batch.changes().size());
        for (FileChange change : batch.changes()) {
            paths.add(change.path());
        }
        if (!paths.isEmpty()) {
            changed(paths);
        }
    }

    private void resourcesChanged(ResourceChange change) {
        if (change.initial()) {
            return;
        }
        List<Path> paths = new ArrayList<>(change.changed().size() + change.removed().size());
        paths.addAll(change.changed());
        paths.addAll(change.removed());
        if (!paths.isEmpty()) {
            changed(paths);
        }
    }

    /**
     * Registers the directory of a script read from a file with the process's file watcher, when the application has
     * one, so that its rebuild is noticed whether or not {@code micronaut.io.watch.paths} covers it.
     *
     * @param file The file of the script, or null for a script not read from a file
     */
    private void watch(@Nullable Path file) {
        if (file == null || registrations.containsKey(file)) {
            return;
        }
        try {
            FileWatcherRegistration registration = watch.watchFile(file, this::filesChanged);
            if (registration != null) {
                registrations.put(file, registration);
            }
        } catch (RuntimeException e) {
            LOG.warn("Could not watch {} for changes: {}", file, e.getMessage());
        }
    }

    /**
     * Waits until a script read from a file has stopped being written, its size and time unchanged over a poll, or for
     * a bounded time.
     *
     * @param desiredPath The configured path of the script
     */
    private void awaitStable(@Nullable String desiredPath) {
        if (desiredPath == null) {
            return;
        }
        Path file;
        try {
            Optional<URL> url = resourceResolver.getResource(desiredPath);
            if (url.isEmpty() || !"file".equals(url.get().getProtocol())) {
                return;
            }
            file = Paths.get(url.get().toURI());
        } catch (Exception e) {
            return;
        }
        long deadline = System.nanoTime() + STABILITY_DEADLINE.toNanos();
        // the size and the time of the file: a bundle rewritten in place at the same size still moves its time
        List<Object> previous = null;
        while (System.nanoTime() < deadline) {
            List<Object> current;
            try {
                current = List.of(Files.size(file), Files.getLastModifiedTime(file));
            } catch (IOException e) {
                current = null;
            }
            if (current != null && current.equals(previous)) {
                return;
            }
            previous = current;
            try {
                Thread.sleep(STABILITY_POLL.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * The file a script was read from, or null for one that did not come from a {@code file:} URL, such as one inside
     * a jar.
     */
    private static @Nullable Path originOf(Source source) {
        URI uri = source.getURI();
        if (uri == null || !"file".equals(uri.getScheme())) {
            return null;
        }
        try {
            return Paths.get(uri).toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            LOG.debug("Could not resolve the origin of {} for file watching", uri, e);
            return null;
        }
    }

    private static @Nullable String stampOf(Source source) {
        URI uri = source.getURI();
        if (uri == null || !"file".equals(uri.getScheme())) {
            return null;
        }
        return digest(source.getCharacters().toString());
    }

    private static @Nullable String stampOf(Path file) {
        try {
            // decoded as the script was read, so that the same file yields the same digest
            return digest(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return null;
        }
    }

    private static String digest(String content) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // every platform has SHA-256; without it every change reloads
            return Long.toString(System.nanoTime());
        }
    }

    /**
     * Stops watching.
     */
    @PreDestroy
    synchronized void close() {
        for (FileWatcherRegistration registration : registrations.values()) {
            registration.close();
        }
        registrations.clear();
        for (BeanWatch resourceWatch : resourceWatches) {
            resourceWatch.close();
        }
        resourceWatches.clear();
    }
}
