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

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
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
import reactor.core.publisher.Flux;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected final ViewsResolver viewsResolver;
    protected final ViewsRendererLocator viewsRendererLocator;
    protected final ViewsModelDecorator viewsModelDecorator;

    /**
     * Constructor.
     * @param viewsResolver Views Resolver
     * @param viewsRendererLocator ViewRendererLocator
     * @param viewsModelDecorator Views Model Decorator
     */
    public ViewsFilter(ViewsResolver viewsResolver,
                       ViewsRendererLocator viewsRendererLocator,
                       ViewsModelDecorator viewsModelDecorator) {
        this.viewsResolver = viewsResolver;
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
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
                    LOG.debug("no view found");
                    return Flux.just(response);
                }
                Object body = response.body();
                MediaType type = resolveMediaType(response, body);
                String view = optionalView.get();
                try {
                    Optional<ViewsRenderer> optionalViewsRenderer = viewsRendererLocator.resolveViewsRenderer(view,  type, body);
                    if (!optionalViewsRenderer.isPresent()) {
                        LOG.debug("no view renderer found for media type: {}, ignoring", type.toString());
                        return Flux.just(response);
                    }
                    ModelAndView<?> modelAndView = new ModelAndView<>(view, body instanceof ModelAndView ? ((ModelAndView<?>) body).getModel().orElse(null) : body);
                    viewsModelDecorator.decorate(request, modelAndView);
                    ViewsRenderer viewsRenderer = optionalViewsRenderer.get();
                    if (viewsRenderer instanceof WritableViewsRenderer) {
                        Writable writable = ((WritableViewsRenderer) viewsRenderer).render(view, modelAndView.getModel().orElse(null), request);
                        response.contentType(type);
                        response.body(writable);
                        return Flux.just(response);
                    } else if (viewsRenderer instanceof ReactiveViewsRenderer ) {
                        return ((ReactiveViewsRenderer) viewsRenderer).render(view, modelAndView.getModel().orElse(null), request, response);

                    } else {
                        return Flux.just(response);
                    }

                } catch (ViewNotFoundException e) {
                    return Flux.error(e);
                }
            });
    }


    /**
     * Resolves the response content type for the matched route.
     * @param response HTTP response
     * @param responseBody HTTP Response body
     * @return The resolved content type
     */
    @NonNull
    protected MediaType resolveMediaType(@NonNull HttpResponse<?> response, @Nullable Object responseBody) {
        Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                AnnotationMetadata.class);
        if (!routeMatch.isPresent()) {
            return MediaType.APPLICATION_JSON_TYPE;
        }
        AnnotationMetadata route = routeMatch.get();
        return route.getValue(Produces.class, MediaType.class)
                .orElse((route.getValue(View.class).isPresent() || responseBody instanceof ModelAndView)
                        ? MediaType.TEXT_HTML_TYPE : MediaType.APPLICATION_JSON_TYPE);
    }
}
