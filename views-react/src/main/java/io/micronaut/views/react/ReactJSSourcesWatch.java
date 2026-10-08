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

import io.micronaut.context.BeanContext;
import io.micronaut.context.BeanProvider;
import io.micronaut.context.WatchableBeanContext;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.reload.ResourceKind;
import io.micronaut.context.watch.BeanWatch;
import io.micronaut.context.watch.ResourceChange;
import io.micronaut.context.watch.ResourceSelector;
import io.micronaut.core.annotation.Internal;
import io.micronaut.scheduling.io.watch.FileChangeBatch;
import io.micronaut.scheduling.io.watch.FileWatcher;
import io.micronaut.scheduling.io.watch.FileWatcherRegistration;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The watches that reload the scripts of {@link ReactJSSources} when their files change, in development mode only.
 * Outside it the scripts are read once, and reloaded only on a {@code FileChangedEvent} of {@code micronaut.io.watch}.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
final class ReactJSSourcesWatch {
    private final BeanProvider<FileWatcher> fileWatchers;
    private final BeanContext beanContext;

    /**
     * @param fileWatchers The process's file watcher, when the application has one
     * @param beanContext The context, whose resource watch reports the launcher's changes
     */
    ReactJSSourcesWatch(BeanProvider<FileWatcher> fileWatchers, BeanContext beanContext) {
        this.fileWatchers = fileWatchers;
        this.beanContext = beanContext;
    }

    /**
     * Watches the resources the development launcher reports: a script under one of its resource roots is read from
     * there, and the launcher reports its edits. Configuration is refreshed rather than watched, so a script among it
     * is left to the file watcher.
     *
     * @param listener The listener of a change
     * @return The watches, to close
     */
    List<BeanWatch> watchResources(Consumer<ResourceChange> listener) {
        List<BeanWatch> watches = new ArrayList<>();
        if (beanContext instanceof WatchableBeanContext watchable) {
            for (ResourceKind kind : List.of(ResourceKind.VIEWS, ResourceKind.STATIC, ResourceKind.OTHER)) {
                watches.add(watchable.watchResources(ResourceSelector.of(kind), listener::accept));
            }
        }
        return watches;
    }

    /**
     * Registers the directory of a script read from a file with the process's file watcher, when the application
     * has one, for that file only.
     *
     * @param file The file of the script
     * @param listener The listener of a change
     * @return The registration, or null without a file watcher
     */
    @Nullable FileWatcherRegistration watchFile(Path file, Consumer<FileChangeBatch> listener) {
        Path directory = file.getParent();
        Path name = file.getFileName();
        if (directory == null || name == null || !fileWatchers.isPresent()) {
            return null;
        }
        return fileWatchers.get().directory(directory).recursive(false).include(name.toString()).watch(listener::accept);
    }
}
