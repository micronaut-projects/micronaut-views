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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

/**
 * Message representation.
 * @author Sergio del Amo
 * @since 4.1.0
 */
public interface Message extends Comparable<Message> {

    @Override
    default int compareTo(Message o) {
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
        return this.code().compareTo(o.code());
    }

    /**
     *
     * @return The i18n code which can be used to fetch a localized message.
     */
    @Nullable
    String code();

    /**
     *
     * @return The default message to use if no code is specified or no localized message found
     */
    @NonNull
    String defaultMessage();

    /**
     *
     * @param defaultMessage The default message to use if no code is specified or no localized message found
     * @param code The i18n code which can be used to fetch a localized message.
     * @return A {@link Message} instance backed by a {@link SimpleMessage}.
     */
    @NonNull
    static Message of(@NonNull String defaultMessage,
                      @Nullable String code) {
        return new SimpleMessage(defaultMessage, code);
    }

    /**
     *
     * @param defaultMessage The default message to use if no code is specified or no localized message found
     * @return A {@link Message} instance backed by a {@link SimpleMessage}.
     */
    @NonNull
    static Message of(@NonNull String defaultMessage) {
        return of(defaultMessage, null);
    }
}
