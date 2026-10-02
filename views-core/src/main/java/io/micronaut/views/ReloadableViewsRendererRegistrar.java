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
package io.micronaut.views;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;

import java.util.Set;

/**
 * Registers the resource watches of every {@link ReloadableViewsRenderer} as it is created, one per
 * extension, in development mode only. A watch registered while a bean is created belongs to that bean,
 * so the renderer's watches close with it.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@Requires(condition = DevelopmentMode.Active.class)
@SuppressWarnings("rawtypes")
final class ReloadableViewsRendererRegistrar implements BeanCreatedEventListener<ReloadableViewsRenderer> {

    @Override
    public ReloadableViewsRenderer onCreated(BeanCreatedEvent<ReloadableViewsRenderer> event) {
        ReloadableViewsRenderer<?, ?> renderer = event.getBean();
        Set<String> extensions = renderer.extensions();
        if (extensions.isEmpty()) {
            ViewsWatches.watch(event.getSource(), renderer::reload);
        } else {
            for (String extension : extensions) {
                ViewsWatches.watch(event.getSource(), renderer::reload, ViewsWatches.globOf(extension));
            }
        }
        return renderer;
    }
}
