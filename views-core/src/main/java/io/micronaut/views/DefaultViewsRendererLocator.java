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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.order.OrderUtil;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.exceptions.ViewNotFoundException;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link ViewsRendererLocator}.
 *
 * @author Sergio del Amo
 * @since 3.0.0
 */
@Singleton
public class DefaultViewsRendererLocator implements ViewsRendererLocator {

    private final Map<ViewsRendererKey, ViewsRenderer> viewsRendererMap = new ConcurrentHashMap<>();

    private final ApplicationContext applicationContext;

    public DefaultViewsRendererLocator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @NonNull
    public Optional<ViewsRenderer> resolveViewsRenderer(@NonNull String view,
                                                        @NonNull MediaType mediaType,
                                                        @Nullable Object body) throws ViewNotFoundException {
        Class<?> bodyClass = body != null ? body.getClass() : null;
        String contentType = mediaType.toString();
        ViewsRendererKey key = new ViewsRendererKey(view, contentType, bodyClass);
        return Optional.ofNullable(viewsRendererMap.computeIfAbsent(key, (viewsRendererKey -> {
            List<ViewsRenderer> viewsRenderers = resolveViewsRenderer(bodyClass, contentType);
            if (viewsRenderers.isEmpty()) {
                return null;
            }
            Optional<ViewsRenderer> result = viewsRenderers.stream()
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
    private List<ViewsRenderer> resolveViewsRenderer(Class<?> bodyClass, @NonNull String contentType) {
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
                    return annotation.getValue(String.class).get().equals(contentType);
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
}
