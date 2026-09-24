/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.views.react.dev;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.order.Ordered;
import io.micronaut.views.react.ReactJSSourcesChangedEvent;
import io.micronaut.views.react.ReactViewsRendererConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Turns a bundle reload into something the transports can hand to a browser.
 *
 * <p>Shared by both, so a rebuild reaches a browser listening over either.
 */
@Requires(bean = ReactDevConfiguration.class)
@Singleton
final class ReactDevReloadBroadcaster implements ApplicationEventListener<ReactJSSourcesChangedEvent>, Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(ReactDevReloadBroadcaster.class);


    /**
     * Identifies the current state of the bundle. A page carries the token it was rendered with,
     * and reloads when the server reports a different one.
     *
     * <p>Comparing tokens rather than just reacting to an event is what makes this survive the
     * reload itself. The browser drops its connection while navigating, and a rebuild announced in
     * that window would be missed; worse, the page can come back having been rendered before the
     * new bundle was swapped in. Replaying the latest token to every new subscriber closes both:
     * a page that is already current sees its own token and does nothing, and a stale one reloads
     * again until it is current.
     */
    private final AtomicLong token = new AtomicLong();

    /**
     * Replays the latest token, so a browser that has just reconnected learns the current state
     * immediately rather than waiting for the next rebuild.
     */
    private final Sinks.Many<String> rebuilds = Sinks.many().replay().latest();

    private static final Duration STABILITY_POLL = Duration.ofMillis(150);
    private static final Duration STABILITY_DEADLINE = Duration.ofSeconds(10);

    private final Duration quietPeriod;

    /** The bundle to watch settle, or {@code null} when it is not a file. */
    private final Path bundle;

    /** The announcement waiting for writes to settle, restarted by each further change. */
    private Disposable pending;  // L(this)

    ReactDevReloadBroadcaster(ReactDevConfiguration configuration,
                              ReactViewsRendererConfiguration viewsConfiguration) {
        this.quietPeriod = configuration.getQuietPeriod();
        this.bundle = bundleFile(viewsConfiguration);
        rebuilds.tryEmitNext(currentToken());
    }

    @Override
    public void onApplicationEvent(ReactJSSourcesChangedEvent event) {
        token.incrementAndGet();
        announceOnceWritesSettle();
    }

    /**
     * Announces the rebuild once nothing further has changed for the quiet period, restarting the
     * wait if another change arrives.
     *
     * <p>See {@link ReactDevConfiguration#getQuietPeriod()} for why announcing immediately does not
     * work: the browser reloads faster than the bundler finishes writing.
     */
    private synchronized void announceOnceWritesSettle() {
        if (pending != null) {
            pending.dispose();
        }
        pending = Mono.delay(quietPeriod)
            .publishOn(Schedulers.boundedElastic())
            .doOnNext(ignored -> awaitStableBundle())
            .subscribe(ignored -> rebuilds.tryEmitNext(currentToken()));
    }

    /**
     * Blocks until the bundle file has stopped growing, or the deadline passes.
     *
     * <p>A fixed wait is a guess at how long the bundler takes, and the measured failure is exactly
     * what happens when the guess is short: the browser reloads, that render re-reads a file still
     * being written, and the stale content is cached as the new bundle. Watching the file settle
     * asks the real question instead. The deadline is there so a bundle being written continuously
     * cannot wedge this forever.
     */
    private void awaitStableBundle() {
        if (bundle == null) {
            return;
        }
        long deadline = System.nanoTime() + STABILITY_DEADLINE.toNanos();
        long previous = -1;
        while (System.nanoTime() < deadline) {
            long current = sizeOf(bundle);
            if (current >= 0 && current == previous) {
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
        LOG.debug("dev reload: {} was still changing after {}, announcing anyway", bundle, STABILITY_DEADLINE);
    }

    private static long sizeOf(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * @return the bundle as a file to watch settle, or {@code null} when it is not one -- a bundle
     * on the classpath does not change under a running application
     */
    private static Path bundleFile(ReactViewsRendererConfiguration configuration) {
        String path = configuration.getServerBundlePath();
        if (path == null || !path.startsWith("file:")) {
            return null;
        }
        try {
            return Paths.get(path.substring("file:".length())).toAbsolutePath().normalize();
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Runs after every other listener of the event.
     *
     * <p>views-react drops its pool of GraalJS contexts from a listener of the same event, and
     * listener order is otherwise unspecified. Telling the browser first is a race it loses: it
     * reloads, is served a render from a context still holding the old bundle, and then sits there
     * -- its token now current, so nothing tells it again. Measured in a real browser, which
     * reloaded exactly once and kept showing the old page.
     *
     * @return the lowest precedence, so every other listener of the event has already run
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * @return the token identifying the bundle as it stands, to bake into a rendered page.
     */
    String currentToken() {
        return Long.toString(token.get());
    }

    /**
     * @return the current token, then one per rebuild of the server bundle.
     */
    Flux<String> rebuilds() {
        return rebuilds.asFlux();
    }
}
