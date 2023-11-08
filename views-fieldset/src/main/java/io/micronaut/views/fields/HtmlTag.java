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
 * Constants for HTML Tags.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public enum HtmlTag {

    /**
     * HTML Tag input.
     */
    INPUT("input"),

    /**
     * HTML Tag select.
     */
    SELECT("select"),

    /**
     * HTML Tag div.
     */
    DIV("div"),

    /**
     * HTML Tag textarea.
     */
    TEXTAREA("textarea"),

    /**
     * HTML Tag option.
     */
    OPTION("option"),

    /**
     * HTML Tag label.
     */
    LABEL("label"),

    /**
     * Trix editor tag.
     * @see <a href="https://trix-editor.org">Trix Editor</a>
     */
    TRIX_EDITOR("trix-editor");

    private final String tag;

    /**
     *
     * @param tag HTML Tag
     */
    HtmlTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}
