/*
 * Copyright 2017-2020 original authors
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

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.util.Toggleable;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Defines Views configuration properties.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
public interface ViewsConfiguration extends Toggleable {

    /**
     * @return The resources' folder where views should be searched for.
     */
    String getFolder();

    /**
     * The directories view sources are read from in development mode, ahead of the class path, each the
     * source directory of the {@link #getFolder() views folder}, such as {@code src/main/resources/views}.
     * Used in development mode only, beside the views roots the development launcher reports.
     *
     * @return The source roots
     * @since 6.4.0
     */
    @Experimental
    @NonNull
    default List<String> getSourceRoots() {
        return List.of();
    }
}
