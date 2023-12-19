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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.http.multipart.CompletedFileUpload;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.elements.InputFileFormElement;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Resolves a form element of type {@link InputFileFormElement} if the bean property is {@link CompletedFileUpload}.
 * @author Sergio del Amo
 * @since 5.1.0
 */
@Requires(classes = CompletedFileUpload.class)
@Singleton
public class CompletedFileUploadFormElementResolver implements FormElementResolver {
    @Override
    public <B, T> Optional<Class<? extends FormElement>> resolve(BeanProperty<B, T> beanProperty) {
        return beanProperty.getType() == CompletedFileUpload.class
                ? Optional.of(InputFileFormElement.class)
                : Optional.empty();
    }

    @Override
    public int getOrder() {
        return DefaultFormElementResolver.ORDER + 10;
    }
}
