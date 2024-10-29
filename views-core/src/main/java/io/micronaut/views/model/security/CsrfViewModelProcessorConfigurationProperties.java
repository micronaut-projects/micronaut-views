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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.security.csrf.CsrfConfiguration;

@ConfigurationProperties(CsrfViewModelProcessorConfigurationProperties.PREFIX)
public class CsrfViewModelProcessorConfigurationProperties implements CsrfViewModelProcessorConfiguration {
    public static final String PREFIX = CsrfConfiguration.PREFIX + ".views-model-decorator";

    /**
     * The default csrfTokenKey value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_CSRF_TOKEN = "csrfToken";

    @NonNull
    private String csrfTokenKey = DEFAULT_CSRF_TOKEN;

    /**
     *
     * @return Model key for CSRF Token.
     */
    public @NonNull String getCsrfTokenKey() {
        return csrfTokenKey;
    }

    /**
     * Model key for CSRF Token. Default value ({@value #DEFAULT_CSRF_TOKEN}).
     *
     * @param csrfTokenKey the key which will be used in the map model.
     */
    public void setCsrfTokenKey(@NonNull String csrfTokenKey) {
        this.csrfTokenKey = csrfTokenKey;
    }
}
