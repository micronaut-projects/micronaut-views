/*
 * Copyright 2017-2023 original authors
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
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;
import java.util.Optional;

/**
 * Default implementation of {@link ViewsResolver}.
 * It checks if the route is annotated with {@link View} or returns a {@link ModelAndView} instance.
 * @author Sergio del Amo
 * @since 3.0.0
 */
@Singleton
@Requires(classes = HttpRequest.class)
public class DefaultViewsResolver implements ViewsResolver {
    @Override
    @NonNull
    public Optional<String> resolveView(HttpRequest<?> request, HttpResponse<?> response) {
        Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                AnnotationMetadata.class);
        if (!routeMatch.isPresent()) {
            return Optional.empty();
        }

        return resolveView(routeMatch.get(), response.body());
    }

    /**
     * Resolves the view for the given method and response body. Subclasses can override to customize.
     *
     * @param route        Request route
     * @param responseBody Response body
     * @return view name to be rendered
     */
    @SuppressWarnings({"WeakerAccess", "unchecked", "rawtypes"})
    protected Optional<String> resolveView(AnnotationMetadata route, @Nullable Object responseBody) {
        Optional<?> optionalViewName = route.getValue(View.class);
        if (optionalViewName.isPresent()) {
            return Optional.of((String) optionalViewName.get());
        } else if (responseBody instanceof ModelAndView) {
            return ((ModelAndView) responseBody).getView();
        }
        return Optional.empty();
    }
}
