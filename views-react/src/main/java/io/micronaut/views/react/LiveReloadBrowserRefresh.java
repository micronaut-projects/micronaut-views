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

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.order.Ordered;
import io.micronaut.dev.livereload.LiveReloadTrigger;
import jakarta.inject.Singleton;

/**
 * Hands a rebuild of the server bundle to the LiveReload server of the development launcher: once the bundle
 * stopped being written and the new one is loaded, the browsers connected to the server reload. Loading first is
 * what the bundle token of {@code micronaut-views-react-dev} guards against otherwise: a browser that reloads before
 * the new bundle is read is served the old markup, and would not be told again.
 *
 * <p>Only present when {@code micronaut-dev} is on the class path and the context is a development one, which has a
 * {@link LiveReloadTrigger}. {@code micronaut-views-react} does not depend on it.</p>
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@Requires(classes = LiveReloadTrigger.class)
@Requires(beans = LiveReloadTrigger.class)
final class LiveReloadBrowserRefresh implements ReactBrowserRefresh, ApplicationEventListener<ReactJSSourcesChangedEvent>, Ordered {

    private final LiveReloadTrigger trigger;
    private final ReactJSSources sources;

    LiveReloadBrowserRefresh(LiveReloadTrigger trigger, ReactJSSources sources) {
        this.trigger = trigger;
        this.sources = sources;
    }

    @Override
    public boolean isActive() {
        return trigger.isEnabled();
    }

    @Override
    public void onApplicationEvent(ReactJSSourcesChangedEvent event) {
        if (trigger.isEnabled() && sources.loadChanged(event.generation())) {
            trigger.reload();
        }
    }

    /**
     * Runs after every other listener of the event, once the renderer dropped its contexts of the old bundle.
     *
     * @return The lowest precedence
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
