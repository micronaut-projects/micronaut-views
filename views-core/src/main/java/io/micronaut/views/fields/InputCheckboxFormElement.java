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
import io.micronaut.core.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox">Input Checkbox</a>
 */
@Introspected(builder = @Introspected.IntrospectionBuilder(builderClass = InputCheckboxFormElement.Builder.class))
public class InputCheckboxFormElement extends FormElement {
    @NonNull
    private final List<Checkbox> checkboxes;

    /**
     *
     * @param checkboxes Checkboxes
     */
    public InputCheckboxFormElement(@NonNull List<Checkbox> checkboxes) {
        this.checkboxes = checkboxes;
    }

    public List<Checkbox> getCheckboxes() {
        return checkboxes;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputCheckboxFormElement that)) return false;

        return Objects.equals(checkboxes, that.checkboxes);
    }

    @Override
    public int hashCode() {
        return checkboxes != null ? checkboxes.hashCode() : 0;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @Nullable
        private List<Checkbox> checkboxes;

        @NonNull
        public Builder checkbox(Checkbox checkbox) {
            if (checkboxes == null) {
                this.checkboxes = new ArrayList<>();
            }
            checkboxes.add(checkbox);
            return this;
        }

        @NonNull
        public Builder checkboxes(List<Checkbox> checkboxes) {
            this.checkboxes = checkboxes;
            return this;
        }

        @NonNull
        public InputCheckboxFormElement build() {
            return new InputCheckboxFormElement(checkboxes == null ? Collections.emptyList() : checkboxes);
        }
    }
}
