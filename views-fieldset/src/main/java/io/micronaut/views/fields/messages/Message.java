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
package io.micronaut.views.fields.messages;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.util.StringUtils;
import jakarta.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;

/**
 * Message representation. It features an optional {@link Message#code()} to allow localization.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param defaultMessage The default message to use if no code is specified or no localized message found.
 * @param code The i18n code which can be used to fetch a localized message.
 */
@Experimental
public record Message(@NonNull String defaultMessage, @Nullable String code) implements Comparable<Message> {
    private static final String REGEX = "(.)([A-Z])";
    private static final String REPLACEMENT = "$1 $2";
    private static final String DOT = ".";

    @Override
    public int compareTo(Message o) {
        int compare = defaultMessage().compareTo(o.defaultMessage());
        if (compare != 0) {
            return compare;
        }
        if (this.code() == null && o.code() != null) {
            return -1;
        }
        if (this.code() != null && o.code() == null) {
            return 1;
        }
        if (this.code() == null) {
            return 0;
        }
        return this.code().compareTo(o.code());
    }

    /**
     *
     * @param defaultMessage The default message to use if no code is specified or no localized message found
     * @param code The i18n code which can be used to fetch a localized message.
     * @return A {@link Message} instance.
     */
    @NonNull
    public static Message of(@NonNull String defaultMessage,
                      @Nullable String code) {
        return new Message(defaultMessage, code);
    }

    /**
     *
     * @param defaultMessage The default message to use if no code is specified or no localized message found
     * @return A {@link Message} instance.
     */
    @NonNull
    public static Message of(@NonNull String defaultMessage) {
        return of(defaultMessage, null);
    }

    /**
     *
     * @param type Field Type
     * @param name Field name
     * @return A {@link Message} instance backed by a {@link SimpleMessage}.
     */
    @NonNull
    public static Message of(Class<?> type, String name) {
        String nameCode = (isAllUpperCase(name) ? name.toLowerCase() : name);
        String defaultMessage = StringUtils.capitalize(nameCode.replaceAll(REGEX, REPLACEMENT));
        String code = type.getSimpleName().toLowerCase() + DOT + nameCode;
        return of(defaultMessage, code);
    }

    /**
     *
     * @param beanProperty A Bean Property
     * @return A {@link Message} instance.
     */
    @NonNull
    public static Message of(@NonNull BeanProperty<?, ?> beanProperty) {
        return of(beanProperty.getDeclaringBean().getBeanType(), beanProperty.getName());
    }

    /**
     *
     * @param violation Constraint Violation
     * @return A {@link Message} instance.
     */
    @NonNull
    public static Message of(@NonNull ConstraintViolation<?> violation) {
        List<String> parts = new ArrayList<>(3);
        parts.add(violation.getLeafBean().getClass().getSimpleName());
        ConstraintViolationUtils.lastNode(violation).ifPresent(parts::add);
        ConstraintViolationUtils.constraintCode(violation).ifPresent(parts::add);
        return of(violation.getMessage(), String.join(DOT, parts.stream().map(String::toLowerCase).toList()));
    }

    private static boolean isAllUpperCase(@NonNull String input) {
        for (char c : input.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }
}
