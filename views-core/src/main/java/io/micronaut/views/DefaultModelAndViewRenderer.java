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
package io.micronaut.views;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Default implementation of {@link ModelAndViewRenderer}.
 * <p>
 * Given a {@link ModelAndView} it will find the view by name, and render it with the model.
 *
 * @author Tim Yates
 * @since 6.0.0
 */
@Singleton
//@Requires(classes = HttpRequest.class)
@Internal
public class DefaultModelAndViewRenderer implements ModelAndViewRenderer {

    protected final ViewsRendererLocator viewsRendererLocator;
    private final ViewsModelDecorator viewsModelDecorator;

    public DefaultModelAndViewRenderer(
        ViewsRendererLocator viewsRendererLocator,
        ViewsModelDecorator viewsModelDecorator
    ) {
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
    }

    @Override
    @NonNull
    public Optional<Writable> render(ModelAndView<?> modelAndView, HttpRequest<?> request) {
        return modelAndView.getView()
            .flatMap(viewName -> {
                viewsModelDecorator.decorate(request, modelAndView);
                Object model = modelAndView.getModel().orElse(null);
                return viewsRendererLocator.resolveViewsRenderer(viewName, MediaType.TEXT_HTML, model)
                    .map(renderer -> renderer.render(viewName, model, request));
            });
    }
}
