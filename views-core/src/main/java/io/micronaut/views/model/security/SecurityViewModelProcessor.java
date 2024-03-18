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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.model.ViewModelProcessor;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Returns information about the current user so that it can be appended to the model being rendered.
 * @param <R> The request type
 * @author Sergio del Amo
 * @since 1.1.0
 */
@Requires(property = SecurityViewModelProcessorConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(beans = {SecurityFilter.class, SecurityViewModelProcessorConfiguration.class})
@Requires(classes = HttpRequest.class)
@Singleton
public class SecurityViewModelProcessor implements ViewModelProcessor<Map<String, Object>, HttpRequest<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityViewModelProcessor.class);

    private final SecurityViewModelProcessorConfiguration securityViewModelProcessorConfiguration;

    /**
     * @param securityViewModelProcessorConfiguration The Security Views Model Decorator configuration
     */
    @Inject
    public SecurityViewModelProcessor(SecurityViewModelProcessorConfiguration securityViewModelProcessorConfiguration) {
        this.securityViewModelProcessorConfiguration = securityViewModelProcessorConfiguration;
    }

    /**
     * @param securityViewModelProcessorConfiguration The Security Views Model Decorator configuration
     * @param securityService                         Utility to access Security information
     * @deprecated Use {@link #SecurityViewModelProcessor(SecurityViewModelProcessorConfiguration)} instead
     */
    @Deprecated(forRemoval = true, since = "5.2.0")
    public SecurityViewModelProcessor(
        SecurityViewModelProcessorConfiguration securityViewModelProcessorConfiguration,
        SecurityService securityService
    ) {
        this(securityViewModelProcessorConfiguration);
    }

    @Override
    public void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Map<String, Object>> modelAndView) {
        request.getAttribute(SecurityFilter.AUTHENTICATION, Authentication.class).ifPresent(authentication -> {
            Map<String, Object> securityModel = new HashMap<>();
            securityModel.put(securityViewModelProcessorConfiguration.getPrincipalNameKey(), authentication.getName());
            securityModel.put(securityViewModelProcessorConfiguration.getAttributesKey(), authentication.getAttributes());

            Map<String, Object> viewModel = modelAndView.getModel().orElseGet(() -> {
                final HashMap<String, Object> newModel = new HashMap<>(1);
                modelAndView.setModel(newModel);
                return newModel;
            });
            try {
                viewModel.putIfAbsent(securityViewModelProcessorConfiguration.getSecurityKey(), securityModel);
            } catch (UnsupportedOperationException ex) {
                final HashMap<String, Object> modifiableModel = new HashMap<>(viewModel);
                modifiableModel.putIfAbsent(securityViewModelProcessorConfiguration.getSecurityKey(), securityModel);
                modelAndView.setModel(modifiableModel);
            }
        });
    }
}
