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
package io.micronaut.views.fields;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Objects;

/**
 * Simple implementation of {@link Message}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected
public class SimpleMessage implements Message {

    @Nullable
    private final String code;

    @NonNull
    private final String defaultMessage;

    /**
     *
     * @param defaultMessage The default message to use if no code is specified or no localized message found.
     * @param code The i18n code which can be used to fetch a localized message.
     */
    public SimpleMessage(@NonNull String defaultMessage,
                         @Nullable String code) {
        this.defaultMessage = defaultMessage;
        this.code = code;
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message that)) return false;

        if (!Objects.equals(code, that.getCode())) return false;
        return Objects.equals(defaultMessage, that.getDefaultMessage());
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (defaultMessage != null ? defaultMessage.hashCode() : 0);
        return result;
    }

    @Override
    @Nullable
    public String getCode() {
        return code;
    }

    @Override
    @NonNull
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
