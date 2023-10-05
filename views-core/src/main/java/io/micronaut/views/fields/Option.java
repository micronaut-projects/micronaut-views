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

import java.util.Objects;

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/option">Option</a>
 */
public class Option {

    private final boolean disabled;

    private final boolean selected;

    @NonNull
    private final String value;

    @NonNull
    private final Message label;

    /**
     * @param selected If present, this Boolean attribute indicates that the option is initially selected.
     * @param disabled If this Boolean attribute is set, this option is not checkable.
     * @param value The content of this attribute represents the value to be submitted with the form, should this option be selected.
     * @param label This attribute is text for the label indicating the meaning of the option.
     */
    public Option(boolean disabled,
                  boolean selected,
                  @NonNull String value,
                  @NonNull Message label) {
        this.selected = selected;
        this.disabled = disabled;
        this.value = value;
        this.label = label;
    }

    /**
     *
     * @return If present, this Boolean attribute indicates that the option is initially selected.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @return If this Boolean attribute is set, this option is not checkable.
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     *
     * @return The content of this attribute represents the value to be submitted with the form, should this option be selected.
     */
    @NonNull
    public String getValue() {
        return value;
    }

    /**
     *
     * @return This attribute is text for the label indicating the meaning of the option.
     */
    @NonNull
    public Message getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Option option)) return false;

        if (disabled != option.disabled) return false;
        if (selected != option.selected) return false;
        if (!Objects.equals(value, option.value)) return false;
        return Objects.equals(label, option.label);
    }

    @Override
    public int hashCode() {
        int result = (disabled ? 1 : 0);
        result = 31 * result + (selected ? 1 : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        return result;
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean disabled;
        private String value;
        private boolean selected;
        private Message label;

        @NonNull
        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        @NonNull
        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        @NonNull
        public Builder value(@NonNull String value) {
            this.value = value;
            return this;
        }


        @NonNull
        public Builder label(Message label) {
            this.label = label;
            return this;
        }

        @NonNull
        public Option build() {
            return new Option(disabled, selected, value, label);
        }
    }

}
