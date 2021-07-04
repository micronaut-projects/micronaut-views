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

import io.micronaut.context.BeanLocator;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.*;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.views.exceptions.ViewNotFoundException;
import io.micronaut.views.model.ViewModelProcessor;
import io.micronaut.web.router.qualifier.ProducesMediaTypeQualifier;
import io.reactivex.Flowable;
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
@Filter("/**")
public class ViewsFilter implements HttpServerFilter {
    private static final Logger LOG = LoggerFactory.getLogger(ViewsFilter.class);

    protected final BeanLocator beanLocator;
    private final Collection<ViewModelProcessor> viewModelProcessors;

    /**
     * Constructor.
     *
     * @param beanLocator The bean locator
     * @param viewModelProcessors Collection of views model decorator beans
     */
    public ViewsFilter(BeanLocator beanLocator,
                       Collection<ViewModelProcessor> viewModelProcessors) {
        this.beanLocator = beanLocator;
        this.viewModelProcessors = viewModelProcessors;
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.RENDERING.order();
    }

    @Override
    public final Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request,
                                                            ServerFilterChain chain) {

        return Flowable.fromPublisher(chain.proceed(request))
            .switchMap(response -> {
                Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                        AnnotationMetadata.class);
                if (routeMatch.isPresent()) {
                    AnnotationMetadata route = routeMatch.get();

                    Object body = response.body();
                    Optional<String> optionalView = resolveView(route, body);

                    if (optionalView.isPresent()) {

                        MediaType type = route.getValue(Produces.class, MediaType.class)
                                .orElse((route.getValue(View.class).isPresent() || body instanceof ModelAndView) ? MediaType.TEXT_HTML_TYPE : MediaType.APPLICATION_JSON_TYPE);
                        Optional<ViewsRenderer> optionalViewsRenderer = beanLocator.findBean(ViewsRenderer.class,
                                new ProducesMediaTypeQualifier<>(type));

                        if (!optionalViewsRenderer.isPresent()) {
                            LOG.debug("no view renderer found for media type: {}, ignoring", type);
                            return Flowable.just(response);
                        }

                        ViewsRenderer viewsRenderer = optionalViewsRenderer.get();

                        ModelAndView<?> modelAndView;

                        // We treat Map<String, Object> the same as a model view as long as
                        // the method includes a view annotation:
                        if (body instanceof ModelAndView || body instanceof Map) {
                            Map<String, Object> context = populateModel(request, viewsRenderer, body);
                            modelAndView  = processModelAndView(request, optionalView.get(), context);
                        } else {
                            // these arbitrary models do not get processed by the view model processors:
                            modelAndView = new ModelAndView<>(optionalView.get(), body);
                        }

                        if (modelAndView.getView().isPresent()) {
                            String view = modelAndView.getView().get();
                            if (viewsRenderer.exists(view)) {
                                Writable writable = viewsRenderer.render(view, modelAndView.getModel().orElse(null), request);
                                response.contentType(type);
                                response.body(writable);
                                return Flowable.just(response);
                            } else {
                                return Flowable.error(new ViewNotFoundException("View [" + view + "] does not exist"));
                            }
                        }
                    }
                }

                return Flowable.just(response);
            });
    }

    /**
     *
     * @param request The HTTP Request being processed
     * @param view The resolved View.
     * @param model The Model returned
     * @return A {@link ModelAndView} after being processed by the available {@link ViewModelProcessor}s.
     */
    protected ModelAndView<Map<String, Object>> processModelAndView(HttpRequest<?> request, String view, Map<String, Object> model) {
        ModelAndView<Map<String, Object>> modelAndView = new ModelAndView<>(
                view,
                model
        );
        if (CollectionUtils.isNotEmpty(viewModelProcessors)) {
            for (ViewModelProcessor modelDecorator : viewModelProcessors) {
                modelDecorator.process(request, modelAndView);
            }
        }
        return modelAndView;
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

}
