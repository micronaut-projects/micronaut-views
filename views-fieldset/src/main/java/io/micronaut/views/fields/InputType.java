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

import io.micronaut.core.annotation.Experimental;

/**
 * Constants for the value of the type attribute in an HTML input tag.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public enum InputType {
    /**
     * HTML Input type radio.
     */
    RADIO("radio"),

    /**
     * HTML Input type submit.
     */
    SUBMIT("submit"),

    /**
     * HTML Input type hidden.
     */
    HIDDEN("hidden"),

    /**
     * HTML Input type number.
     */
    NUMBER("number"),

    /**
     * HTML Input type date.
     */
    DATE("date"),

    /**
     * HTML Input type time.
     */
    TIME("time"),

    /**
     * HTML Input type datetime-local.
     */
    DATE_TIME_LOCAL("datetime-local"),

    /**
     * HTML Input type checkbox.
     */
    CHECKBOX("checkbox"),

    /**
     * HTML Input type email.
     */
    EMAIL("email"),

    /**
     * HTML Input type file.
     */
    FILE("file"),

    /**
     * HTML Input type password.
     */
    PASSWORD("password"),

    /**
     * HTML Input type url.
     */
    URL("url"),

    /**
     * HTML Input type text.
     */
    TEXT("text"),

    /**
     * HTML Input type tel.
     */
    TEL("tel");

    private final String type;

    /**
     *
     * @param type HTML input type
     */
    InputType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
