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

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.order.Ordered;
import io.micronaut.views.fields.FormElement;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Optional;

/**
 * Composite Pattern implementaiton for the {@link FormElementResolver} API.
 * This instance is injected when injecting a single bean of type {@link FormElementResolver} because of the {@link Primary} annotation.
 * This implementation iterates through every bean of type {@link FormElementResolver} in order and returns the first resolved {@link FormElement} class if any.
 * @author Sergio del Amo
 * @since 5.1.0
 */
@Experimental
@Primary
@Singleton
@Internal
public class CompositeFormElementResolver implements FormElementResolver {

    private final List<FormElementResolver> formElementResolvers;

    /**
     *
     * @param formElementResolvers Beans of type {@link FormElementResolver} in order.
     */
    public CompositeFormElementResolver(List<FormElementResolver> formElementResolvers) {
        this.formElementResolvers = formElementResolvers;
    }

    @Override
    public <B, T> Optional<Class<? extends FormElement>> resolve(BeanProperty<B, T> beanProperty) {
        Optional<? extends Class<? extends FormElement>> optional = formElementResolvers.stream()
                .map(resolver -> resolver.resolve(beanProperty))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        if (optional.isPresent()) {
            Class<? extends FormElement> result = optional.get();
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
