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

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.views.exceptions.ViewNotFoundException;
import io.micronaut.views.model.ViewModelProcessor;
import reactor.core.publisher.Flux;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Templates Filter.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
@Requires(beans = ViewsRenderer.class)
@Requires(beans = ViewsResolver.class)
@Filter(Filter.MATCH_ALL_PATTERN)
public class ViewsFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ViewsFilter.class);

    protected final ApplicationContext applicationContext;
    protected final ViewsRendererLocator viewsRendererLocator;
    protected final ViewsResolver viewsResolver;

    /**
     * Constructor.
     *
     * @param applicationContext Application context
     * @param viewsRendererLocator ViewsRenderer locator
     * @param viewsResolver Views Resolver
     */
    public ViewsFilter(ApplicationContext applicationContext,
                       ViewsRendererLocator viewsRendererLocator,
                       ViewsResolver viewsResolver) {
        this.applicationContext = applicationContext;
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsResolver = viewsResolver;
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.RENDERING.order();
    }

    @Override
    public final Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                            ServerFilterChain chain) {
        return Flux.from(chain.proceed(request))
            .switchMap(response -> {
                Optional<String> optionalView = viewsResolver.resolveView(request, response);
                if (!optionalView.isPresent()) {
                    return Flux.just(response);
                }
                Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                        AnnotationMetadata.class);
                if (!routeMatch.isPresent()) {
                    return Flux.just(response);
                }
                AnnotationMetadata route = routeMatch.get();
                Object body = response.body();
                MediaType type = resolveMediaType(route, body);
                String view = optionalView.get();
                try {
                    Optional<ViewsRenderer> optionalViewsRenderer = viewsRendererLocator.resolveViewsRenderer(view,  type, body);
                    if (!optionalViewsRenderer.isPresent()) {
                        LOG.debug("no view renderer found for media type: {}, ignoring", type.toString());
                        return Flux.just(response);
                    }
                    ViewsRenderer viewsRenderer = optionalViewsRenderer.get();
                    ModelAndView<?> modelAndView;
                    if (body instanceof ModelAndView || body instanceof Map) {
                        modelAndView = new ModelAndView<>(view, populateModel(request, viewsRenderer, body));
                    } else {
                        modelAndView = new ModelAndView<>(view, body);
                    }
                    enhanceModel(request, modelAndView);
                    Writable writable = viewsRenderer.render(view, modelAndView.getModel().orElse(null), request);
                    response.contentType(type);
                    response.body(writable);
                    return Flux.just(response);

                } catch (ViewNotFoundException e) {
                    return Flux.error(e);
                }
            });
    }

    /**
     * Resolves the response content type for the matched route.
     * @param route The matched route
     * @param body The response body
     * @return The resolved content type
     */
    @NonNull
    protected MediaType resolveMediaType(@NonNull AnnotationMetadata route,
                                         @Nullable Object body) {
        return route.getValue(Produces.class, MediaType.class)
                .orElse((route.getValue(View.class).isPresent() || body instanceof ModelAndView)
                        ? MediaType.TEXT_HTML_TYPE : MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Resolves the model for the given response body and enhances the model with instances of {@link ViewModelProcessor}.
     * @param request {@link HttpRequest} being processed
     * @param viewsRenderer The Views rendered being used to render the view
     * @param responseBody Response Body
     * @return A model with the controllers response and enhanced with the decorators.
     */
    @SuppressWarnings("unused")
    protected Map<String, Object> populateModel(HttpRequest<?> request, ViewsRenderer viewsRenderer, Object responseBody) {
        return new HashMap<>(viewsRenderer.modelOf(resolveModel(responseBody)));
    }

    /**
     * Resolves the model for the given response body. Subclasses can override to customize.
     *
     * @param responseBody Response body
     * @return the model to be rendered
     */
    @SuppressWarnings({"WeakerAccess", "unchecked", "rawtypes"})
    protected Object resolveModel(Object responseBody) {
        if (responseBody instanceof ModelAndView) {
            return ((ModelAndView) responseBody).getModel().orElse(null);
        }
        return responseBody;
    }

    /**
     * Enhances a model by running it by all applicable ViewModelProcessors {@link ViewModelProcessor}.
     *
     * @param request      The http request this model relates to.
     * @param modelAndView The ModelAndView to be enhanced.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void enhanceModel(HttpRequest<?> request, @NonNull ModelAndView<?> modelAndView) {
        if (modelAndView.getModel().isPresent()) {
            Collection<ViewModelProcessor> processors = applicationContext.getBeansOfType(ViewModelProcessor.class,
                    Qualifiers.byTypeArguments(modelAndView.getModel().get().getClass())
            );
            if (LOG.isDebugEnabled()) {
                LOG.debug("located {} view model processors", processors.size());
            }
            processors.forEach(it -> it.process(request, modelAndView));
        }
    }
}
