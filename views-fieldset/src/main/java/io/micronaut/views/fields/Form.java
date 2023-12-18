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
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.validation.constraints.Pattern;

/**
 * Representation of an HTML form.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param action Form Action
 * @param method Form Method. For example `post`
 * @param fieldset Form fields
 * @param enctype how the form-data should be encoded when submitting it to the server
 */
@Experimental
@Introspected
public record Form(@NonNull String action,
                   @Nullable @Pattern(regexp = "get|post") String method,
                   @NonNull Fieldset fieldset,
                   @Nullable @Pattern(regexp = "application/x-www-form-urlencoded|multipart/form-data|text/plain") String enctype) {

    private static final String POST = "post";

    public Form(@NonNull String action, @NonNull String method, @NonNull Fieldset fieldset) {
        this(action, method, fieldset, null);
    }

    public Form(@NonNull String action, @NonNull Fieldset fieldset) {
        this(action, POST, fieldset, null);
    }

    public Form(@NonNull String action,
                @NonNull Fieldset fieldset,
                @Nullable String enctype) {
        this(action, POST, fieldset, enctype);
    }

    public Form(@NonNull String action,
                @NonNull String method,
                @NonNull Fieldset fieldset,
                @Nullable String enctype) {
        if (enctype != null && !method.equals(POST)) {
            throw new IllegalArgumentException("enctype attribute can be used only if method equals post");
        }
        this.action = action;
        this.method = method;
        this.fieldset = fieldset;
        this.enctype = enctype;
    }
}
