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

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.turbo.TurboFrameView;
import io.micronaut.views.turbo.TurboView;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;

import java.util.Optional;

/**
 * Holder for both Model and View.
 *
 * @param <T> The model type
 * @author Sergio del Amo
 * @author graemerocher
 * @since 1.0
 */
public class ModelAndView<T> {

    private String view;

    private T model;

    private String contentType = MediaType.TEXT_HTML;

    /**
     * Empty constructor.
     */
    public ModelAndView() {
    }

    /**
     * Constructor.
     *
     * @param view  view name to be rendered
     * @param model Model to be rendered against the view
     */
    public ModelAndView(String view, T model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Build a ModelAndView from the matched route (if any).
     *
     * @param <T>      The model type
     * @param request  The request
     * @param response The response
     * @return The model and view
     */
    @NonNull
    public static <T> Optional<ModelAndView<T>> of(@NonNull HttpRequest<?> request, @NonNull MutableHttpResponse<?> response) {
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
            .flatMap(routeMatch -> of(request, routeMatch));
    }

    @NonNull
    private static <T> Optional<ModelAndView<T>> of(@NonNull HttpRequest<?> request, @NonNull AnnotationMetadata route) {
        // Not a view
        if (!route.hasAnnotation(View.class)) {
            return Optional.empty();
        }

        // Handled by TurboStream#of
        if (TurboMediaType.acceptsTurboStream(request) && route.hasAnnotation(TurboView.class)) {
            return Optional.empty();
        }

        // Handled by TurboFrame#of
        Optional<String> turboFrameOptional = request.getHeaders().get(TurboHttpHeaders.TURBO_FRAME, String.class);
        if (turboFrameOptional.isPresent() && route.hasAnnotation(TurboFrameView.class)) {
            return Optional.empty();
        }

        ModelAndView<T> modelAndView = new ModelAndView<>();
        route.stringValue(View.class).ifPresent(modelAndView::setView);
        modelAndView.setContentType(route.stringValue(Produces.class).orElse(MediaType.TEXT_HTML));
        return Optional.of(modelAndView);
    }

    /**
     * @return view name to be rendered
     */
    @NonNull
    public Optional<String> getView() {
        return Optional.ofNullable(view);
    }

    /**
     * Sets the view to use.
     *
     * @param view the view name
     */
    public void setView(String view) {
        this.view = view;
    }

    /**
     * @return model to render
     */
    public Optional<T> getModel() {
        return Optional.ofNullable(model);
    }

    /**
     * Sets the model to use.
     *
     * @param model model to be rendered
     */
    public void setModel(T model) {
        this.model = model;
    }

    /**
     * @return The content type to render
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type to render.
     *
     * @param contentType The content type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
