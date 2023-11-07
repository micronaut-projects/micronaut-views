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
import io.micronaut.views.fields.messages.Message;

import java.util.List;

/**
 * HTML Form Element Attributes.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public interface FormElementAttributes {

    /**
     *
     * @return Name of the form control. Submitted with the form as part of a name/value pair
     */
    @NonNull
    String name();

    /**
     *
     * @return represents a caption for an item in a user interface
     */
    @Nullable
    Message label();

    /**
     *
     * @return Form element validation Errors.
     */
    @NonNull
    List<Message> errors();

    /**
     *
     * @return Whether the form element has validation errors
     */
    default boolean hasErrors() {
        return !errors().isEmpty();
    }
}
