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
import jakarta.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@link Message} implementation backed by a {@link ConstraintViolation}.
 * @param defaultMessage The default message to use if no code is specified or no localized message found.
 * @param code The i18n code which can be used to fetch a localized message.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected
public record ConstraintViolationMessage(@NonNull String code, @NonNull String defaultMessage) implements Message {

    private static final String DOT = ".";

    /**
     *
     * @param constraintViolation Constraint Violation.
     */
    public ConstraintViolationMessage(ConstraintViolation<?> constraintViolation) {
        this(code(constraintViolation), constraintViolation.getMessage());
    }

    @SuppressWarnings("NeedBraces")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message that)) return false;

        if (!Objects.equals(code, that.code())) return false;
        return Objects.equals(defaultMessage, that.defaultMessage());
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (defaultMessage != null ? defaultMessage.hashCode() : 0);
        return result;
    }

    private static String code(ConstraintViolation<?> violation) {
        List<String> parts = new ArrayList<>();
        parts.add(violation.getLeafBean().getClass().getSimpleName());
        ConstraintViolationUtils.lastNode(violation).ifPresent(parts::add);
        ConstraintViolationUtils.constraintCode(violation).ifPresent(parts::add);
        return String.join(DOT, parts.stream().map(String::toLowerCase).toList());
    }

    @Override
    public String toString() {
        return "Message{" +
            "code='" + code + '\'' +
            ", defaultMessage='" + defaultMessage + '\'' +
            '}';
    }
}
