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
package io.micronaut.views.fields.controllers;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.core.type.Argument;
import io.micronaut.data.annotation.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Internal
public final class IdUtils {
    private static final Logger LOG = LoggerFactory.getLogger(IdUtils.class);

    private IdUtils() {
    }

    @NonNull
    public static Optional<Class<?>> idType(@NonNull BeanIntrospection<?> introspection) {
        for (BeanProperty<?, ?> beanProperty : introspection.getBeanProperties()) {
            if (beanProperty.hasAnnotation(Id.class)) {
                return Optional.of(beanProperty.getType());
            }
        }
        return Optional.empty();
    }

    @NonNull
    public static <T> Optional<String> id(T entity) {
        try {
            BeanWrapper<T> wrapper = BeanWrapper.getWrapper(entity);
            for (BeanProperty<T, ?> beanProperty : wrapper.getBeanProperties()) {
                if (beanProperty.hasAnnotation(Id.class)) {
                    return beanProperty.get(entity, Argument.STRING);
                }
            }
        } catch (IntrospectionException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resource {} is not introspected", entity.getClass());
            }
        }
        return Optional.empty();
    }
}
