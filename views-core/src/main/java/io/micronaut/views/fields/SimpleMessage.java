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

import java.util.Objects;

@Introspected
public class SimpleMessage implements Message {

    @NonNull
    private final String code;

    @NonNull
    private final String defaultMessage;

    public SimpleMessage(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message that)) return false;

        if (!Objects.equals(code, that.getCode())) return false;
        return Objects.equals(defaultMessage, that.getDefaultMessage());
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (defaultMessage != null ? defaultMessage.hashCode() : 0);
        return result;
    }

    @Override
    @NonNull
    public String getCode() {
        return code;
    }

    @Override
    @NonNull
    public String getDefaultMessage() {
        return defaultMessage;
    }


    @Override
    public String toString() {
        return "Message{" +
            "code='" + code + '\'' +
            ", defaultMessage='" + defaultMessage + '\'' +
            '}';
    }
}
