/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.views.fields;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.security.csrf.CsrfConfiguration;
import io.micronaut.security.csrf.repository.CsrfTokenRepository;
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * If a CSRF Token can be resolved, it adds a hidden field to the list of fields with the name {@link CsrfConfiguration#getFieldName()}. and the value the CSRF Token.
 * @since 5.6.0
 * @author Sergio del Amo
 */
@Requires(beans = { CsrfTokenRepository.class, CsrfTokenRepository.class })
@Internal
@Singleton
final class CsrfFieldsetConsumer implements FieldsetConsumer {
    private final CsrfTokenRepository<HttpRequest<?>> csrfTokenRepository;
    private final CsrfConfiguration csrfConfiguration;

    CsrfFieldsetConsumer(CsrfTokenRepository<HttpRequest<?>> csrfTokenRepository, CsrfConfiguration csrfConfiguration) {
        this.csrfTokenRepository = csrfTokenRepository;
        this.csrfConfiguration = csrfConfiguration;
    }

    @Override
    public void accept(@NonNull List<FormElement> formElements) {
        ServerRequestContext.currentRequest()
                .flatMap(csrfTokenRepository::findCsrfToken)
                .ifPresent(t -> formElements.add(csrfInputHidden(t)));
    }

    @NonNull
    private InputHiddenFormElement csrfInputHidden(@NonNull String csrfToken) {
        return InputHiddenFormElement.builder()
                .name(csrfConfiguration.getFieldName())
                .value(csrfToken)
                .build();
    }
}
