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
package io.micronaut.views.fields.messages;

import io.micronaut.context.LocalizedMessageSource;
import io.micronaut.core.annotation.NonNull;
import java.util.Optional;

/**
 * Utility class to resolve message of a {@link Message} with a given {@link LocalizedMessageSource}.
 * @author Sergio del Amo
 * @since 5.6.0
 */
public class LocalizedMessageResolver {

    private final LocalizedMessageSource messageSource;

    public LocalizedMessageResolver(LocalizedMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     *
     * @param message The message
     * @return the resolved message or the defaultMessage if the message is not found.
     */
    @NonNull
    public String getMessageOrDefault(@NonNull Message message) {
        return messageSource.getMessageOrDefault(message.code(), message.defaultMessage(), message.variables());
    }

    /**
     *
     * @param message The message
     * @return the resolved message or an empty optional if not found.
     */
    @NonNull
    public Optional<String> getMessage(@NonNull Message message) {
        return messageSource.getMessage(message.code(), message.variables());
    }
}
