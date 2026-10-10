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
package io.micronaut.views.rocker;

import com.fizzed.rocker.RockerModel;
import io.micronaut.core.annotation.Internal;

import java.util.Optional;

/**
 * The Rocker templates of the views source roots in development mode, compiled at runtime. Kept apart from the
 * compiler, which needs {@code rocker-compiler}, so that the engine links without it.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
interface SourceRootsTemplates extends AutoCloseable {

    /**
     * The model of a template under a source root, compiled with every other template of the roots when one
     * changed since the last compilation.
     *
     * @param templatePath The template path, such as {@code views/home.rocker.html}
     * @return The model, or empty when no source root holds the template
     * @throws RuntimeException when the templates do not compile
     */
    Optional<RockerModel> model(String templatePath);

    /**
     * Compiles the templates again on the next lookup.
     */
    void invalidate();

    @Override
    void close();
}
