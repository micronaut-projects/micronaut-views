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
package io.micronaut.views.reactive;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.order.OrderUtil;
import io.micronaut.http.annotation.Produces;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewNotFoundException;
import jakarta.inject.Singleton;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link ReactiveViewsRendererLocator}.
 *
 * @author Sergio del Amo
 * @since 3.0.0
 */
@Singleton
@Internal
final class DefaultReactiveViewsRendererLocator implements ReactiveViewsRendererLocator {

    private final Map<ViewsRendererKey, ReactiveViewsRenderer> viewsRendererMap = new ConcurrentHashMap<>();

    private final BeanContext beanContext;

    public DefaultReactiveViewsRendererLocator(ApplicationContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    @NonNull
    public Optional<ReactiveViewsRenderer> resolveViewsRenderer(@NonNull String view,
                                                        @NonNull String contentType,
                                                        @Nullable Object body) throws ViewNotFoundException {
        Class<?> bodyClass = body != null ? body.getClass() : null;
        ViewsRendererKey key = new ViewsRendererKey(view, contentType, bodyClass);
        return Optional.ofNullable(viewsRendererMap.computeIfAbsent(key, (viewsRendererKey -> {
            List<ReactiveViewsRenderer> viewsRenderers = resolveViewsRenderer(bodyClass, contentType);
            if (viewsRenderers.isEmpty()) {
                return null;
            }
            Optional<ReactiveViewsRenderer> result = viewsRenderers.stream()
                    .filter(viewsRenderer -> viewsRenderer.exists(view))
                    .findFirst();
            if (result.isPresent()) {
                return result.get();
            }
            throw new ViewNotFoundException("View [" + view + "] does not exist");
        })));
    }

    /**
     *
     * @param bodyClass Response Body Class
     * @param contentType Response Content Type
     * @return List of {@link ViewsRenderer} which includes those which do not specify an {@link @Produces} annotation or
     * whose {link @Produces} annotation value matches the response content type. The list is sorted. The order is those {@link ViewsRenderer} which
     * type argument matches the response body class first and then ordered by {@link OrderUtil#COMPARATOR}.
     */
    @NonNull
    private List<ReactiveViewsRenderer> resolveViewsRenderer(Class<?> bodyClass, @NonNull String contentType) {
        return reactiveViewsRenderersByBodyClass(bodyClass)
                .stream()
                .filter(vr -> producesContentType(contentType, vr))
                .sorted((o1, o2) -> {
                    BeanDefinition o1BeanDefinition = beanDefinitionForViewRenderer(o1);
                    BeanDefinition o2BeanDefinition = beanDefinitionForViewRenderer(o2);
                    if (o1BeanDefinition.getTypeArguments().size() != o2BeanDefinition.getTypeArguments().size()) {
                        return Integer.compare(o1BeanDefinition.getTypeArguments().size(), o2BeanDefinition.getTypeArguments().size());
                    }
                    return OrderUtil.COMPARATOR.compare(o1, o2);
                }).toList();
    }

    private Collection<ReactiveViewsRenderer> reactiveViewsRenderersByBodyClass(Class<?> bodyClass) {
        if (bodyClass == null) {
            return beanContext.getBeansOfType(ReactiveViewsRenderer.class);
        }
        Collection<ViewsRenderer> viewsRenderers = beanContext.getBeansOfType(ViewsRenderer.class, Qualifiers.byTypeArguments(bodyClass, Object.class));
        return beanContext.getBeansOfType(ReactiveViewsRenderer.class, Qualifiers.byTypeArguments(bodyClass, Object.class, Object.class))
            .stream()
            .filter(reactiveViewsRenderer -> {
                if (reactiveViewsRenderer instanceof ReactiveViewsRendererAdapter<?, ?> adapter) {
                    return viewsRenderers.stream()
                            .map(vr -> vr.getClass())
                            .anyMatch(clazz -> clazz == adapter.getDelegateClass());
                }
                return true;

            })
            .toList();
    }

    private BeanDefinition beanDefinitionForViewRenderer(ReactiveViewsRenderer reactiveViewsRenderer) {
        if (reactiveViewsRenderer instanceof ReactiveViewsRendererAdapter<?, ?> adapter) {
            return beanContext.getBeanDefinition(adapter.getDelegateClass());
        }
        return beanContext.getBeanDefinition(reactiveViewsRenderer.getClass());
    }

    private boolean producesContentType(@NonNull String contentType, ReactiveViewsRenderer reactiveViewsRenderer) {
        BeanDefinition beanDefinition = beanDefinitionForViewRenderer(reactiveViewsRenderer);
        return producesContentType(contentType, beanDefinition);
    }

    private boolean producesContentType(@NonNull String contentType, BeanDefinition beanDefinition) {
        AnnotationValue<Produces> annotation = beanDefinition.getAnnotation(Produces.class);
        if (annotation == null) {
            return true;
        }
        return Arrays.asList(annotation.stringValues()).contains(contentType);
    }

    record ViewsRendererKey(String viewName, String contentType, Class<?> bodyClass) {

    }
}
