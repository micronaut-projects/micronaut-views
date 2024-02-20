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
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.turbo.TurboFrame;
import io.micronaut.views.turbo.TurboView;

import java.util.Optional;

import static io.micronaut.views.turbo.http.TurboMediaType.TURBO_STREAM_TYPE;

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
     * @param request  The request
     * @param response The response
     * @return The model and view
     */
    public static Optional<ModelAndView> of(HttpRequest<?> request, MutableHttpResponse<?> response) {
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
            .map(routeMatch -> {
                Object body = response.body();
                // Not a view response
                if (isNotAViewRoute(routeMatch, body)) {
                    return null;
                }
                if (body instanceof TurboFrame.Builder) {
                    return null;
                }
                if (isATurboRequest(request, routeMatch)) {
                    return null;
                }
                ModelAndView modelAndView = new ModelAndView();
                routeMatch.stringValue(View.class).ifPresent(modelAndView::setView);

                modelAndView.setContentType(routeMatch.stringValue(Produces.class).orElse(MediaType.TEXT_HTML));

                if (body instanceof ModelAndView<?> mav) {
                    mav.getView().ifPresent(modelAndView::setView);
                    mav.getModel().ifPresent(modelAndView::setModel);
                } else {
                    modelAndView.setModel(body);
                }

                return modelAndView;
            });
    }

    private static boolean isATurboRequest(HttpRequest<?> request, AnnotationMetadata routeMatch) {
        return routeMatch.hasAnnotation(TurboView.class) && request.accept().contains(TURBO_STREAM_TYPE);
    }

    private static boolean isNotAViewRoute(AnnotationMetadata routeMatch, Object body) {
        return !(body instanceof ModelAndView) && !routeMatch.hasAnnotation(View.class);
    }

    /**
     * @return view name to be rendered
     */
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
