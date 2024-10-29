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
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.views.model.MapViewModelProcessor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Returns information about the current user so that it can be appended to the model being rendered.
 *
 * @author Sergio del Amo
 * @since 1.1.0
 */
@Requires(property = SecurityViewModelProcessorConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(beans = {SecurityFilter.class, SecurityViewModelProcessorConfiguration.class})
@Requires(classes = HttpRequest.class)
@Singleton
public class SecurityViewModelProcessor extends MapViewModelProcessor {
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
    protected void populateModel(HttpRequest<?> request, Map<String, Object> model) {
        request.getAttribute(SecurityFilter.AUTHENTICATION, Authentication.class)
                .ifPresent(authentication -> {
                    Map<String, Object> securityModel = securityModel(authentication);
                    model.put(securityViewModelProcessorConfiguration.getSecurityKey(), securityModel);
        });
    }

    private Map<String, Object> securityModel(Authentication authentication) {
        Map<String, Object> securityModel = new HashMap<>();
        securityModel.put(securityViewModelProcessorConfiguration.getPrincipalNameKey(), authentication.getName());
        securityModel.put(securityViewModelProcessorConfiguration.getAttributesKey(), authentication.getAttributes());
        return securityModel;
    }
}
