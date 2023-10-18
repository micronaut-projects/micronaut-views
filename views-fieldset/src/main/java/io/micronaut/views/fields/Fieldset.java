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
import io.micronaut.core.util.CollectionUtils;

import java.util.List;

/**
 * Represents a HTML fieldset.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/fieldset">The Fieldset element</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Introspected
public class Fieldset {

    @NonNull
    private final List<? extends FormElement> fields;

    @NonNull
    private final List<Message> errors;

    /**
     *
     * @param fields Fieldset fields
     * @param errors Global Validation errors
     */
    public Fieldset(@NonNull List<? extends FormElement> fields,
                    @NonNull List<Message> errors) {
        this.fields = fields;
        this.errors = errors;
    }

    /**
     *
     * @return fieldset fields
     */
    @NonNull
    public List<? extends FormElement> getFields() {
        return fields;
    }

    /**
     *
     * @return Global validation errors
     */
    @NonNull
    public List<Message> getErrors() {
        return errors;
    }

    /**
     *
     * @return Whether there are any global validation errors
     */
    public boolean hasErrors() {
        return CollectionUtils.isNotEmpty(errors);
    }
}
