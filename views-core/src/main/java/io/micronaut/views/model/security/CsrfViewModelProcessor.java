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
package io.micronaut.views.model.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.csrf.repository.CsrfTokenRepository;
import jakarta.inject.Singleton;

import java.util.Map;

/**
 * Adds a CSRF Token to the model map if a CSRF Token can be retrieved via {@link CsrfTokenRepository}.
 *
 * @author Sergio del Amo
 * @since 5.6.0
 */
@Requires(property = CsrfViewModelProcessor.ENABLED, notEquals = StringUtils.FALSE)
@Requires(beans = {CsrfTokenRepository.class, CsrfViewModelProcessorConfiguration.class})
@Requires(classes = HttpRequest.class)
@Singleton
@Internal
final class CsrfViewModelProcessor implements MapViewModelProcessor {
    /**
     * Property to enable/disable the CsrfViewModelProcessor.
     */
    public static final String ENABLED = CsrfViewModelProcessorConfigurationProperties.PREFIX + ".enabled";

    private final CsrfTokenRepository<HttpRequest<?>> csrfTokenRepository;
    private final CsrfViewModelProcessorConfiguration csrfViewModelProcessorConfiguration;

    /**
     * @param csrfViewModelProcessorConfiguration CSRF Views Model Decorator configuration
     * @param csrfTokenRepository The CSRF Token Repository
     */
    public CsrfViewModelProcessor(CsrfViewModelProcessorConfiguration csrfViewModelProcessorConfiguration,
                                  CsrfTokenRepository<HttpRequest<?>> csrfTokenRepository) {
        this.csrfViewModelProcessorConfiguration = csrfViewModelProcessorConfiguration;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    public void populateModel(HttpRequest<?> request, Map<String, Object> model) {
        csrfTokenRepository.findCsrfToken(request)
                .ifPresent(csrfToken -> model.put(csrfViewModelProcessorConfiguration.getCsrfTokenKey(), csrfToken));
    }
}
