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
 * Constants for the value of the type attribute in an HTML input tag.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
public final class InputType {
    /**
     * HTML Input type radio.
     */
    public static final String ATTR_TYPE_RADIO = "radio";

    /**
     * HTML Input type submit.
     */
    public static final String ATTR_TYPE_SUBMIT = "submit";

    /**
     * HTML Input type hidden.
     */
    public static final String ATTR_TYPE_HIDDEN = "hidden";

    /**
     * HTML Input type number.
     */
    public static final String ATTR_TYPE_NUMBER = "number";

    /**
     * HTML Input type date.
     */
    public static final String ATTR_TYPE_DATE = "date";

    /**
     * HTML Input type time.
     */
    public static final String ATTR_TYPE_TIME = "time";

    /**
     * HTML Input type datetime-local.
     */
    public static final String ATTR_TYPE_DATE_TIME_LOCAL = "datetime-local";

    /**
     * HTML Input type checkbox.
     */
    public static final String ATTR_TYPE_CHECKBOX = "checkbox";

    /**
     * HTML Input type email.
     */
    public static final String ATTR_TYPE_EMAIL = "email";

    /**
     * HTML Input type password.
     */
    public static final String ATTR_TYPE_PASSWORD = "password";

    /**
     * HTML Input type url.
     */
    public static final String ATTR_TYPE_URL = "url";

    /**
     * HTML Input type text.
     */
    public static final String ATTR_TYPE_TEXT = "text";

    /**
     * HTML Input type tel.
     */
    public static final String ATTR_TYPE_TEL = "tel";

    private InputType() {
    }
}
