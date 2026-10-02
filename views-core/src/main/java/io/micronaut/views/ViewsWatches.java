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

import io.micronaut.context.BeanContext;
import io.micronaut.context.WatchableBeanContext;
import io.micronaut.context.env.DevelopmentMode;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.BeanWatch;
import io.micronaut.context.watch.ResourceSelector;
import io.micronaut.context.watch.ResourceWatcher;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.value.PropertyResolver;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Registers the resource watches of views-core, in development mode only.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class ViewsWatches {

    private ViewsWatches() {
    }

    /**
     * Whether views follow edits: the context is in development mode and can be watched.
     *
     * @param beanContext The context
     * @return True if it is
     */
    static boolean isActive(@Nullable BeanContext beanContext) {
        return beanContext instanceof WatchableBeanContext
            && beanContext instanceof PropertyResolver propertyResolver
            && DevelopmentMode.isEnabled(propertyResolver);
    }

    /**
     * Watches the views files a selector selects, when views follow edits.
     *
     * @param beanContext The context
     * @param globs The globs, relative to a views root; none for every file
     * @param watcher The watcher
     * @return The watch, or null when nothing was registered
     */
    @Nullable
    static BeanWatch watch(@Nullable BeanContext beanContext, @NonNull ResourceWatcher watcher, String... globs) {
        if (!isActive(beanContext)) {
            return null;
        }
        return ((WatchableBeanContext) beanContext).watchResources(ResourceSelector.of(ResourceKind.VIEWS, globs), watcher);
    }

    /**
     * The glob of the files with an extension, at any depth.
     *
     * @param extension The extension, with or without the dot
     * @return The glob
     */
    @NonNull
    static String globOf(@NonNull String extension) {
        String bare = extension.startsWith(".") ? extension.substring(1) : extension;
        return "**/*." + bare;
    }
}
