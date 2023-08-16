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

import java.util.List;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#attributes">Input Attributes</a>
 */
public interface InputAttributes {
    @Nullable
    String getId();

    /**
     * The name of the form control. Submitted with the form as part of a name/value pair.
     * @return the Name of the form control.
     */
    @NonNull
    String getName();

    @Nullable
    String getValue();

    @NonNull
    InputType getType();

    /**
     *
     * @return Whether a value is required or must be checked for the form to be submittable.
     */
    boolean isRequired();


    @NonNull
    List<Message> getErrors();

    boolean hasErrors();


    @Nullable
    Message getLabel();
}
