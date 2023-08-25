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
package io.micronaut.views.jstachio;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStachePath;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.inject.BeanConfiguration;
import io.micronaut.views.DefaultViewsResolver;
import io.micronaut.views.ViewsResolver;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves view name by checking the annotations {@link JStache} and {@link JStacheConfig} in the response body.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Requires(classes = HttpRequest.class)
@Replaces(DefaultViewsResolver.class)
@Singleton
public class JstachioViewResolver implements ViewsResolver {
    private static final Logger LOG = LoggerFactory.getLogger(JstachioViewResolver.class);
    private static final String MEMBER_PATH = "path";
    private static final String MEMBER_PATHING = "pathing";
    private static final String MEMBER_PREFIX = "prefix";
    private static final String MEMBER_SUFFIX = "suffix";
    private static final String MEMBER_USING = "using";
    private final BeanContext beanContext;
    private final ConcurrentHashMap<Class<?>, String> viewForClass = new ConcurrentHashMap<>();

    /**
     *
     * @param beanContext BeanContext
     */
    public JstachioViewResolver(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public Optional<String> resolveView(HttpRequest<?> request, HttpResponse<?> response) {
        Object body = response.body();
        return body == null ?
            Optional.empty() :
            Optional.ofNullable(viewForClass.computeIfAbsent(body.getClass(), c -> resolveView(c).orElse(null)));
    }

    private Optional<String> resolveView(Class<?> bodyType) {
        try {
            BeanIntrospection<?> introspection = BeanIntrospection.getIntrospection(bodyType);
            if (introspection.hasAnnotation(JStache.class)) {
                AnnotationValue<JStache> jStacheAnnotation = introspection.getAnnotation(JStache.class);
                Optional<AnnotationValue<JStacheConfig>> jStacheConfigAnnotationValueOptional = resolveJStacheConfig(introspection);
                if (jStacheConfigAnnotationValueOptional.isEmpty()) {
                    return jStacheAnnotation.stringValue(MEMBER_PATH);
                }
                return jStacheConfigAnnotationValueOptional.flatMap(jStacheConfigAnnotationValue -> resolveView(jStacheAnnotation, jStacheConfigAnnotationValue));
            }
        } catch (IntrospectionException e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("{} may not be annotated with @Introspected", bodyType.getSimpleName(), e);
            }
        }
        return Optional.empty();
    }

    @NonNull
    private Optional<AnnotationValue<JStacheConfig>> resolveJStacheConfig(@NonNull BeanIntrospection<?> introspection) {
        if (introspection.hasAnnotation(JStacheConfig.class)) {
            AnnotationValue<JStacheConfig> jStacheConfig = introspection.getAnnotation(JStacheConfig.class);
            Optional<Class<?>> usingClassOptional = jStacheConfig.classValue(MEMBER_USING);
            if (usingClassOptional.isPresent()) {
                Class<?> usingClass = usingClassOptional.get();
                try {
                    BeanIntrospection<?> usingIntrospection = BeanIntrospection.getIntrospection(usingClass);
                    if (usingIntrospection.hasAnnotation(JStacheConfig.class)) {
                        return usingIntrospection.findAnnotation(JStacheConfig.class);
                    }
                } catch (IntrospectionException e) {
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Using class {} may not be annotated with @Introspected", usingClass.getSimpleName(), e);
                    }
                }
            }
        }
        Class<?> beanType = introspection.getBeanType();
        return resolveJStacheConfig(beanType);
    }

    @NonNull
    private Optional<AnnotationValue<JStacheConfig>> resolveJStacheConfig(@NonNull Class<?> beanType) {
        Package pkg = beanType.getPackage();
        if (pkg != null) {
            Optional<BeanConfiguration> beanConfigurationOptional = beanContext.findBeanConfiguration(pkg.getName());
            if (beanConfigurationOptional.isPresent()) {
                BeanConfiguration beanConfiguration = beanConfigurationOptional.get();
                return Optional.ofNullable(beanConfiguration.getAnnotation(JStacheConfig.class));
            }
        }
        return Optional.empty();
    }

    @NonNull
    private Optional<String> resolveView(@NonNull AnnotationValue<JStache> jStacheAnnotation,
                                         @NonNull AnnotationValue<JStacheConfig> jStacheConfigAnnotationValue) {
        Optional<AnnotationValue<JStachePath>> pathingAnnotationValueOptional = jStacheConfigAnnotationValue.getAnnotation(MEMBER_PATHING, JStachePath.class);
        if (pathingAnnotationValueOptional.isPresent()) {
            AnnotationValue<JStachePath> jStachePathAnnotationValue = pathingAnnotationValueOptional.get();
            Optional<String> prefixOptional = jStachePathAnnotationValue.stringValue(MEMBER_PREFIX);
            Optional<String> suffixOptional = jStachePathAnnotationValue.stringValue(MEMBER_SUFFIX);
            return jStacheAnnotation.stringValue(MEMBER_PATH).map(path -> prefixOptional.orElse("") + path + suffixOptional.orElse(""));
        }
        return Optional.empty();

    }
}
