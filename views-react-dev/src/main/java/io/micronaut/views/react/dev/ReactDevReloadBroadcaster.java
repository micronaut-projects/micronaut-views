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
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Turns a bundle reload into something the transports can hand to a browser.
 *
 * <p>Shared by both, so a rebuild reaches a browser listening over either.
 */
@Requires(bean = ReactDevConfiguration.class)
@Singleton
final class ReactDevReloadBroadcaster implements ApplicationEventListener<ReactJSSourcesChangedEvent>, Ordered {

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

    ReactDevReloadBroadcaster() {
        rebuilds.tryEmitNext(currentToken());
    }

    @Override
    public void onApplicationEvent(ReactJSSourcesChangedEvent event) {
        token.incrementAndGet();
        rebuilds.tryEmitNext(currentToken());
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
