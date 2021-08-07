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
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.exceptions.ViewNotFoundException;
import io.micronaut.views.model.ViewModelProcessor;
import io.micronaut.web.router.qualifier.ProducesMediaTypeQualifier;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Collection;
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

    /**
     * Constructor.
     *
     * @param beanLocator The bean locator
     */
    public ViewsFilter(BeanLocator beanLocator) {
        this.beanLocator = beanLocator;
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

                    // if no route was matched, just return
                    if (!routeMatch.isPresent()) {
                        return Flux.just(response);
                    }

                    // we have a route, let's see if there's a @View annotation or if the response is a ModelView:
                    AnnotationMetadata route = routeMatch.get();
                    response.getBody();
                    Object body = response.body();
                    Optional<String> optionalView = resolveView(route, body);

                    // route match but no view, just return:
                    if (!optionalView.isPresent()) {
                        return Flux.just(response);
                    }

                    // let's figure out what the response media type should be:
                    MediaType type = route.getValue(Produces.class, MediaType.class)
                            .orElse((route.getValue(View.class).isPresent() || body instanceof ModelAndView) ? MediaType.TEXT_HTML_TYPE : MediaType.APPLICATION_JSON_TYPE);

                    // locating view renderer that matches that media type:
                    Optional<ViewsRenderer> optionalViewsRenderer = beanLocator.findBean(ViewsRenderer.class, new ProducesMediaTypeQualifier<>(type));

                    if (!optionalViewsRenderer.isPresent()) {
                        LOG.debug("no view renderer found for media type: {}, ignoring", type);
                        return Flux.just(response);
                    }

                    ViewsRenderer viewsRenderer = optionalViewsRenderer.get();

                    ModelAndView<?> modelAndView;
                    if (body instanceof ModelAndView) {
                        modelAndView = (ModelAndView<?>) body;
                    } else {
                        modelAndView = new ModelAndView<>(optionalView.get(), body);
                    }

                    enhanceModel(request, modelAndView);

                    if (modelAndView.getView().isPresent()) {
                        String view = modelAndView.getView().get();
                        if (viewsRenderer.exists(view)) {
                            Writable writable = viewsRenderer.render(view, modelAndView.getModel().orElse(null), request);
                            response.contentType(type);
                            response.body(writable);
                        } else {
                            return Flux.error(new ViewNotFoundException("View [" + view + "] does not exist"));
                        }
                    }
                    return Flux.just(response);
                });
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
    protected void enhanceModel(HttpRequest<?> request, ModelAndView<?> modelAndView) {
        if (!modelAndView.getModel().isPresent()) {
            return;
        }

        Collection<ViewModelProcessor> processors = beanLocator.getBeansOfType(ViewModelProcessor.class,
                Qualifiers.byTypeArguments(modelAndView.getModel().get().getClass())
        );

        LOG.debug("located {} view model processors", processors.size());

        processors.forEach(it -> it.process(request, modelAndView));

    }

}
