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
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.views.exceptions.ViewNotFoundException;
import io.micronaut.views.exceptions.ViewRenderingException;
import io.micronaut.views.turbo.TurboFrame;
import io.micronaut.views.turbo.TurboFrameRenderer;
import io.micronaut.views.turbo.TurboStreamRenderer;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Templates Filter.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
@Requires(beans = ViewsResolver.class)
@Filter(Filter.MATCH_ALL_PATTERN)
public class ViewsFilter implements HttpServerFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ViewsFilter.class);
    private static final MediaType UTF8_HTML = new MediaType(MediaType.TEXT_HTML, Map.of(MediaType.CHARSET_PARAMETER, "UTF-8"));

    /**
     * Views Resolver.
     */
    protected final ViewsResolver viewsResolver;

    /**
     * Views Render Locator.
     */
    protected final ViewsRendererLocator viewsRendererLocator;

    /**
     * Views Model Decorator.
     */
    protected final ViewsModelDecorator viewsModelDecorator;

    /**
     * Turbo Stream Renderer.
     */
    protected final TurboStreamRenderer turboStreamRenderer;

    /**
     * Turbo Stream Renderer.
     */
    protected final TurboFrameRenderer turboFrameRenderer;

    /**
     * Constructor.
     * @param viewsResolver Views Resolver
     * @param viewsRendererLocator ViewRendererLocator
     * @param viewsModelDecorator Views Model Decorator
     * @param turboFrameRenderer Turbo Frame renderer
     */
    @Inject
    public ViewsFilter(ViewsResolver viewsResolver,
                       ViewsRendererLocator viewsRendererLocator,
                       ViewsModelDecorator viewsModelDecorator,
                       TurboFrameRenderer turboFrameRenderer) {
        this.viewsResolver = viewsResolver;
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
        this.turboStreamRenderer = null;
        this.turboFrameRenderer = turboFrameRenderer;
    }

    /**
     * Constructor.
     * @param viewsResolver Views Resolver
     * @param viewsRendererLocator ViewRendererLocator
     * @param viewsModelDecorator Views Model Decorator
     * @param turboStreamRenderer Turbo Stream renderer
     * @param turboFrameRenderer Turbo Frame renderer
     * @deprecated Use {@link ViewsFilter(ViewsResolver, ViewsRendererLocator, ViewsModelDecorator, TurboFrameRenderer)} instead.
     */
    @Deprecated(forRemoval = true, since = "4.1.0")
    public ViewsFilter(ViewsResolver viewsResolver,
                       ViewsRendererLocator viewsRendererLocator,
                       ViewsModelDecorator viewsModelDecorator,
                       TurboStreamRenderer turboStreamRenderer,
                       TurboFrameRenderer turboFrameRenderer) {
        this.viewsResolver = viewsResolver;
        this.viewsRendererLocator = viewsRendererLocator;
        this.viewsModelDecorator = viewsModelDecorator;
        this.turboStreamRenderer = turboStreamRenderer;
        this.turboFrameRenderer = turboFrameRenderer;
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.RENDERING.order();
    }

    @Override
    public final Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                            ServerFilterChain chain) {
        if (TurboMediaType.acceptsTurboStream(request)) {
            return chain.proceed(request);
        }
        return Flux.from(chain.proceed(request))
            .switchMap(response -> {
                Object body = response.body();
                Optional<Writable> writableOptional = parseTurboFrameWritable(request, response, body);
                if (writableOptional.isPresent()) {
                    return responseForWritable(response, writableOptional.get(), MediaType.TEXT_HTML_TYPE);
                }
                Optional<String> optionalView = viewsResolver.resolveView(request, response);
                if (!optionalView.isPresent()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("no view found");
                    }
                    return Flux.just(response);
                }

                MediaType type = resolveMediaType(request, response, body);
                String view = optionalView.get();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("view resolved: {}", view);
                }

                try {
                    Optional<ViewsRenderer> optionalViewsRenderer = viewsRendererLocator.resolveViewsRenderer(view,  type.getName(), body);
                    if (!optionalViewsRenderer.isPresent()) {
                        LOG.debug("no view renderer found for media type: {}, ignoring", type);
                        return Flux.just(response);
                    }
                    ModelAndView<?> modelAndView = new ModelAndView<>(view, body instanceof ModelAndView ? ((ModelAndView<?>) body).getModel().orElse(null) : body);
                    viewsModelDecorator.decorate(request, modelAndView);
                    Writable writable = optionalViewsRenderer.get().render(view, modelAndView.getModel().orElse(null), request);
                    response.contentType(type);
                    response.body(writable);
                    return Flux.just(response);
                } catch (ViewNotFoundException | ViewRenderingException e) {
                    return Flux.error(e);
                }
            });
    }

    /**
     * Resolves the response content type for the matched route.
     * @param request HTTP Request
     * @param response HTTP response
     * @param responseBody HTTP Response body
     * @return The resolved content type
     */
    @NonNull
    protected MediaType resolveMediaType(@Nullable HttpRequest<?> request, @NonNull HttpResponse<?> response, @Nullable Object responseBody) {
        Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                AnnotationMetadata.class);
        if (routeMatch.isEmpty()) {
            return MediaType.APPLICATION_JSON_TYPE;
        }
        AnnotationMetadata route = routeMatch.get();
        Optional<MediaType> type = request == null
            ? route.getValue(Produces.class, MediaType.class)
            : route.getValue(Produces.class, Argument.listOf(MediaType.class)).orElseGet(Collections::emptyList).stream().filter(mt -> accept(request, mt)).findFirst();
        return type.orElseGet(() -> (route.getValue(View.class).isPresent() || responseBody instanceof ModelAndView) ? UTF8_HTML : MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Resolves the response content type for the matched route.
     * @param response HTTP response
     * @param responseBody HTTP Response body
     * @return The resolved content type
     * @deprecated Use {@link ViewsFilter#resolveMediaType(HttpRequest, HttpResponse, Object)} instead.
     */
    @Deprecated(forRemoval = true, since = "4.1.0")
    protected MediaType resolveMediaType(@NonNull HttpResponse<?> response, @Nullable Object responseBody) {
        return resolveMediaType(null, response, responseBody);
    }

    private static boolean accept(HttpRequest<?> request, MediaType mediaType) {
        List<MediaType> accept = request.getHeaders().accept();
        return accept.isEmpty() || accept.stream().anyMatch(p -> p.equals(mediaType));
    }

    @NonNull
    private Optional<TurboFrame.Builder> parseTurboFrame(@NonNull HttpRequest<?> request,
                                                         @NonNull MutableHttpResponse<?> response) {
        final Object body = response.body();
        return Optional.ofNullable(TurboFrame.Builder.of(request, response)
            .map(builder -> (TurboFrame.Builder) builder.templateModel(body))
            .orElseGet(() -> {
                if (body instanceof TurboFrame.Builder) {
                    return (TurboFrame.Builder) body;
                }
                return null;
            }));
    }

    @NonNull
    private Optional<Writable> parseRenderableBody(@Nullable Object body) {
        return body instanceof Renderable ? ((Renderable) body).render() : Optional.empty();
    }

    @NonNull
    private Optional<Writable> parseTurboFrameWritable(@NonNull HttpRequest<?> request,
                                                       @NonNull MutableHttpResponse<?> response,
                                                       @Nullable Object body) {
        Optional<Writable> optionalWritable = parseTurboFrame(request, response)
                .flatMap(builder -> turboFrameRenderer.render(builder, request));
        return optionalWritable.isPresent() ? optionalWritable : parseRenderableBody(body);

    }

    @NonNull
    private Publisher<MutableHttpResponse<?>> responseForWritable(@NonNull MutableHttpResponse<?> response,
                                                                  @NonNull Writable writable,
                                                                  @NonNull MediaType mediaType) {
        response.body(writable);
        response.contentType(mediaType);
        return Flux.just(response);
    }
}
