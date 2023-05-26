/*
 * Copyright 2017-2021 original authors
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

import io.micronaut.context.annotation.DefaultImplementation;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.exceptions.ViewNotFoundException;

import java.util.Optional;

/**
 * Helps resolve a {@link ViewsRenderer} for a particular view, media type and response class.
 *
 * @author Sergio del Amo
 * @since 3.0.0
 */
@FunctionalInterface
@DefaultImplementation(DefaultViewsRendererLocator.class)
public interface ViewsRendererLocator {
    /**
     *
     * @param view view Name
     * @param mediaType Response Content Type
     * @param body Response Body
     * @return The {@link ViewsRenderer} able to resolve the view with the response body and media type
     * @throws ViewNotFoundException if the view is not found for the resolved {@link ViewsRenderer}s.
     */
    @NonNull
    Optional<ViewsRenderer> resolveViewsRenderer(@NonNull String view,
                                                 @NonNull String mediaType,
                                                 @Nullable Object body) throws ViewNotFoundException;

}
