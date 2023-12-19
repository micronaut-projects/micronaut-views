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
package io.micronaut.views.fields.formelementresolvers;

import io.micronaut.core.annotation.Indexed;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.order.Ordered;
import io.micronaut.views.fields.FormElement;

import java.util.Optional;

/**
 * Resolves given a bean property the {@link FormElement} class which should be used to be build the element.
 * @author Sergio del Amo
 * @since 5.1.0
 */
@FunctionalInterface
@Indexed(FormElementResolver.class)
public interface FormElementResolver extends Ordered {
    /**
     * Resolves given a bean property the {@link FormElement} class which should be used to be build the element.
     *
     * @param beanProperty Bean Property
     * @return The best Form Element for the bean property.
     * @param <B> The bean type
     * @param <T> The bean property type
     */
    <B, T> Optional<Class<? extends FormElement>> resolve(@NonNull BeanProperty<B, T> beanProperty);
}
