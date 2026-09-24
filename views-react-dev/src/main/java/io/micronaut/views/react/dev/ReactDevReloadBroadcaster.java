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

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.views.react.ReactJSSourcesChangedEvent;
import jakarta.inject.Singleton;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Turns a bundle reload into something the transports can hand to a browser.
 *
 * <p>Shared by both, so a rebuild reaches a browser listening over either.
 */
@Singleton
final class ReactDevReloadBroadcaster implements ApplicationEventListener<ReactJSSourcesChangedEvent> {

    /**
     * Best effort on purpose. A rebuild nobody was listening for is not worth replaying: a browser
     * that connects afterwards would reload immediately, for a change it already has.
     */
    private final Sinks.Many<String> rebuilds = Sinks.many().multicast().directBestEffort();

    @Override
    public void onApplicationEvent(ReactJSSourcesChangedEvent event) {
        rebuilds.tryEmitNext(Long.toString(System.currentTimeMillis()));
    }

    /**
     * @return one item per rebuild of the server bundle, carrying the time it happened.
     */
    Flux<String> rebuilds() {
        return rebuilds.asFlux();
    }
}
