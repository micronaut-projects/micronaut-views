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
package io.micronaut.views.fields.elements;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.message.Message;

import java.util.List;

/**
 * API for html input of type string. e.g. input type text, password, url, tel.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public interface InputStringFormElement {

    /**
     *
     * @return Name of the form control. Submitted with the form as part of a name/value pair
     */
    @NonNull
    String name();

    /**
     *
     * @return It defines an identifier (ID) which must be unique in the whole document
     */
    @Nullable
    String id();

    /**
     *
     * @return The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     */
    @Nullable
    String placeholder();

    /**
     *
     * @return If true indicates that the user must specify a value for the input before the owning form can be submitted.
     */
    boolean required();

    /**
     *
     * @return A Boolean attribute which, if present, means this field cannot be edited by the user.
     */
    boolean readOnly();

    /**
     *
     * @return The maximum string length that the user can enter into the text input.
     */
    @Nullable
    Number maxLength();

    /**
     *
     * @return The minimum string length that the user can enter into the text input.
     */
    @Nullable
    Number minLength();

    /**
     *
     * @return The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
     */
    @Nullable
    String pattern();

    /**
     *
     * @return The size attribute is a numeric value indicating how many characters wide the input field should be.
     */
    @Nullable
    Integer size();

    /**
     *
     * @return the input value
     */
    @Nullable
    String value();

    /**
     *
     * @return message for an HTML Label element.
     */
    @Nullable Message label();

    /**
     *
     * @return Input Errors
     */
    @NonNull List<Message> errors();
}
