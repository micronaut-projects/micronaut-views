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
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;
import io.micronaut.core.order.OrderUtil;
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
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Templates Filter.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
@Requires(beans = ViewsRenderer.class)
@Filter(Filter.MATCH_ALL_PATTERN)
public class ViewsFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ViewsFilter.class);

    protected final ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param applicationContext Application Context
     */
    public ViewsFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
                Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                        AnnotationMetadata.class);
                if (!routeMatch.isPresent()) {
                    return Flux.just(response);
                }
                AnnotationMetadata route = routeMatch.get();
                Object body = response.body();
                Optional<String> optionalView = resolveView(route, body);
                if (!optionalView.isPresent()) {
                    return Flux.just(response);
                }
                MediaType type = resolveMediaType(route, body);
                List<ViewsRenderer> viewsRenderers = resolveViewsRenderer(body != null ? body.getClass() : null, type.toString());
                if (viewsRenderers.isEmpty()) {
                    LOG.debug("no view renderer found for media type: {}, ignoring", type);
                    return Flux.just(response);
                }
                String view = optionalView.get();
                Optional<ViewsRenderer> optionalViewsRenderer = viewsRenderers.stream()
                        .filter(viewsRenderer -> viewsRenderer.exists(view))
                        .findFirst();
                if (!optionalViewsRenderer.isPresent()) {
                    return Flux.error(new ViewNotFoundException("View [" + view + "] does not exist"));
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
            });
    }

    /**
     *
     * @param bodyClass Response Body class
     * @param mediaType Response Content Type
     * @return List of {@link ViewsRenderer} which includes those which do not specify an {@link @Produces} annotation or
     * whose {link @Produces} annotation value matches the response content type. The list is sorted. The order is those {@link ViewsRenderer} which
     * type argument matches the response body class first and then ordered by {@link OrderUtil#COMPARATOR}.
     */
    @NonNull
    public List<ViewsRenderer> resolveViewsRenderer(@Nullable Class<?> bodyClass, @NonNull String mediaType) {
        return (bodyClass == null ? applicationContext.getBeansOfType(ViewsRenderer.class) :
                applicationContext.getBeansOfType(ViewsRenderer.class, Qualifiers.byTypeArguments(bodyClass)))
                .stream()
                .filter(viewsRenderer -> {
                    BeanDefinition<? extends ViewsRenderer> beanDefinition = applicationContext.getBeanDefinition(viewsRenderer.getClass());
                    AnnotationValue<Produces> annotation = beanDefinition.getAnnotation(Produces.class);
                    if (annotation == null) {
                        return true;
                    }
                    if (!annotation.getValue(String.class).isPresent()) {
                        return false;
                    }
                    return annotation.getValue(String.class).get().equals(mediaType);
                })
                .sorted((o1, o2) -> {
                    BeanDefinition<? extends ViewsRenderer> o1BeanDefinition = applicationContext.getBeanDefinition(o1.getClass());
                    BeanDefinition<? extends ViewsRenderer> o2BeanDefinition = applicationContext.getBeanDefinition(o2.getClass());
                    if (o1BeanDefinition.getTypeArguments().size() != o2BeanDefinition.getTypeArguments().size()) {
                        return Integer.compare(o1BeanDefinition.getTypeArguments().size(), o2BeanDefinition.getTypeArguments().size());
                    }
                    return OrderUtil.COMPARATOR.compare(o1, o2);
                })
                .collect(Collectors.toList());
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
     * Resolves the view for the given method and response body. Subclasses can override to customize.
     *
     * @param route        Request route
     * @param responseBody Response body
     * @return view name to be rendered
     */
    @SuppressWarnings({"WeakerAccess", "unchecked", "rawtypes"})
    protected Optional<String> resolveView(AnnotationMetadata route, Object responseBody) {
        Optional<?> optionalViewName = route.getValue(View.class);
        if (optionalViewName.isPresent()) {
            return Optional.of((String) optionalViewName.get());
        } else if (responseBody instanceof ModelAndView) {
            return ((ModelAndView) responseBody).getView();
        }
        return Optional.empty();
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
