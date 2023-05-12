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
package io.micronaut.views.model.security;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.filters.SecurityFilter;
import io.micronaut.security.utils.SecurityService;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.model.ViewModelProcessor;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Returns information about the current user so that it can be appended to the model being rendered.
 *
 * @author Sergio del Amo
 * @since 1.1.0
 */
@Requires(property = SecurityViewModelProcessorConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
@Requires(beans = {SecurityFilter.class, SecurityService.class, SecurityViewModelProcessorConfiguration.class})
@Requires(classes = HttpRequest.class)
@Singleton
public class SecurityViewModelProcessor implements ViewModelProcessor<Map<String, Object>> {

    private static final Logger LOG = LoggerFactory.getLogger(SecurityViewModelProcessor.class);

    private final SecurityService securityService;
    private final SecurityViewModelProcessorConfiguration securityViewModelProcessorConfiguration;

    /**
     *
     * @param securityViewModelProcessorConfiguration The Security Views Model Decorator configuration
     * @param securityService Utility to access Security information
     */
    public SecurityViewModelProcessor(SecurityViewModelProcessorConfiguration securityViewModelProcessorConfiguration,
                                      SecurityService securityService) {
        this.securityViewModelProcessorConfiguration = securityViewModelProcessorConfiguration;
        this.securityService = securityService;
    }

    @Override
    public void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Map<String, Object>> modelAndView) {
        Optional<Authentication> authentication = securityService.getAuthentication();
        if (authentication.isPresent()) {
            Map<String, Object> securityModel = new HashMap<>();
            securityModel.put(securityViewModelProcessorConfiguration.getPrincipalNameKey(), authentication.get().getName());
            securityModel.put(securityViewModelProcessorConfiguration.getAttributesKey(), authentication.get().getAttributes());

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
        }
    }
}
