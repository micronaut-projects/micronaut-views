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

import io.micronaut.core.annotation.Internal;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * The source roots of {@link ViewsSourceRoots#none()}.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
enum NoViewsSourceRoots implements ViewsSourceRoots {
    INSTANCE;

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public @NonNull List<Path> roots() {
        return List.of();
    }

    @Override
    public @NonNull Optional<Path> resolve(@NonNull String viewName, @Nullable String extension) {
        return Optional.empty();
    }
}
