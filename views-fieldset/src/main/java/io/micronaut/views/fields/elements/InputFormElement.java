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
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.InputType;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input">Input</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public interface InputFormElement extends FormElement {

    @Override
    @NonNull
    default HtmlTag getTag() {
        return HtmlTag.INPUT;
    }

    /**
     *
     * @return Value of the type attribute of an input tag
     */
    @NonNull
    InputType getType();
}
