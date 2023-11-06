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

import java.util.Collections;
import java.util.List;

/**
 * Base builder class for all InputStringFormElement builders.
 * @param <T> The type of the InputStringFormElement
 * @param <SELF> The type of the subclassed builder
 *
 * @author Tim Yates
 * @since 4.1.0
 */
@SuppressWarnings("java:S119")
public abstract class InputStringFormBuilder<T extends InputStringFormElement, SELF extends InputStringFormBuilder<T, SELF>> {

    protected String name;
    protected String id;
    protected String placeholder;
    protected boolean required;
    protected boolean readOnly;
    protected Number maxLength;
    protected Number minLength;
    protected Integer size;
    protected String pattern;
    protected String value;
    protected List<Message> errors;
    protected Message label;

    /**
     *
     * @param pattern The pattern attribute, when specified, is a regular expression that the input's value must match for the value to pass constraint validation.
     * @return The Builder
     */
    @NonNull
    public SELF pattern(@NonNull String pattern) {
        this.pattern = pattern;
        return self();
    }

    /**
     *
     * @param required If true indicates that the user must specify a value for the input before the owning form can be submitted.
     * @return the Builder
     */
    @NonNull
    public SELF required(boolean required) {
        this.required = required;
        return self();
    }

    /**
     *
     * @param readOnly A Boolean attribute which, if present, means this field cannot be edited by the user.
     * @return the Builder
     */
    @NonNull
    public SELF readOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return self();
    }

    /**
     *
     * @param size The size attribute is a numeric value indicating how many characters wide the input field should be.
     * @return The Builder
     */
    @NonNull
    public SELF size(@NonNull Integer size) {
        this.size = size;
        return self();
    }

    /**
     *
     * @param maxLength The maximum string length that the user can enter into the text input.
     * @return The Builder
     */
    @NonNull
    public SELF maxLength(@NonNull Number maxLength) {
        this.maxLength = maxLength;
        return self();
    }

    /**
     *
     * @param minLength The minimum string length that the user can enter into the text input.
     * @return The Builder
     */
    @NonNull
    public SELF minLength(@NonNull Number minLength) {
        this.minLength = minLength;
        return self();
    }

    /**
     *
     * @param name Name of the form control. Submitted with the form as part of a name/value pair
     * @return the Builder
     */
    @NonNull
    public SELF name(@NonNull String name) {
        this.name = name;
        return self();
    }

    /**
     *
     * @param id It defines an identifier (ID) which must be unique in the whole document
     * @return The Builder
     */
    @NonNull
    public SELF id(@NonNull String id) {
        this.id = id;
        return self();
    }

    /**
     *
     * @param placeholder The placeholder attribute is a string that provides a brief hint to the user as to what kind of information is expected in the field.
     * @return the Builder
     */
    @NonNull
    public SELF placeholder(@NonNull String placeholder) {
        this.placeholder = placeholder;
        return self();
    }

    /**
     *
     * @param value The value attribute of the input element
     * @return the Builder
     */
    @NonNull
    public SELF value(@NonNull String value) {
        this.value = value;
        return self();
    }

    /**
     *
     * @param label represents a caption for an item in a user interface
     * @return the Builder
     */
    @NonNull
    public SELF label(Message label) {
        this.label = label;
        return self();
    }

    /**
     *
     * @param errors Form element validation Errors.
     * @return The builder
     */
    @NonNull
    public SELF errors(@NonNull List<Message> errors) {
        this.errors = errors;
        return self();
    }

    @SuppressWarnings("unchecked")
    private SELF self() {
        return (SELF) this;
    }

    /**
     *
     * @return Creates a {@link InputTelFormElement}.
     */
    @NonNull
    public abstract T build();
}
