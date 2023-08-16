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

/**
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/option">Option</a>
 */
public class Option {

    private final boolean selected;
    private final boolean disabled;

    @Nullable
    private final String label;

    @NonNull
    private final String text;

    @Nullable
    private final String value;



    /**
     * @param selected If present, this Boolean attribute indicates that the option is initially selected.
     * @param disabled If this Boolean attribute is set, this option is not checkable.
     * @param text Text Option text
     * @param value The content of this attribute represents the value to be submitted with the form, should this option be selected.
     * @param label This attribute is text for the label indicating the meaning of the option.
     */
    public Option(@NonNull String text,
                  @Nullable String value,
                  boolean selected,
                  boolean disabled,
                  @Nullable String label) {
        this.selected = selected;
        this.disabled = disabled;
        this.text = text;
        this.label = label;
        this.value = value;
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
     * @return Option Text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @return The content of this attribute represents the value to be submitted with the form, should this option be selected.
     */
    public String getValue() {
        return value;
    }

    /**
     *
     * @return This attribute is text for the label indicating the meaning of the option.
     */
    @Nullable
    public String getLabel() {
        return label;
    }
}
