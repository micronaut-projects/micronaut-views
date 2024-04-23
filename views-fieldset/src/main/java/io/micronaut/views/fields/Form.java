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

import io.micronaut.core.annotation.*;
import io.micronaut.views.fields.constraints.EnctypePostRequired;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Representation of an HTML form.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param action Form Action
 * @param method Form Method. either `get` or `post`
 * @param fieldset Form fields
 * @param enctype how the form-data should be encoded when submitting it to the server
 * @param dataturbo enables Turbo Drive on a form, defaults to not disabled
 */
@Experimental
@EnctypePostRequired
@Introspected
public record Form(@NonNull @NotBlank String action,
                   @NonNull @NotBlank @Pattern(regexp = "get|post") String method,
                   @NonNull @NotNull @Valid Fieldset fieldset,
                   @Nullable @Pattern(regexp = "application/x-www-form-urlencoded|multipart/form-data|text/plain") String enctype,
                   @Nullable Boolean dataturbo) {

    private static final String POST = "post";

    /**
     *
     * @param action Form Action
     * @param method Form Method. either `get` or `post`
     * @param fieldset Form fields
     * @param dataturbo Form data-turbo
     */
    public Form(@NonNull String action,
                @NonNull String method,
                @NonNull Fieldset fieldset,
                @Nullable Boolean dataturbo) {
        this(action, method, fieldset, null, dataturbo);
    }

    /**
     *
     * @param action Form Action
     * @param method Form Method. either `get` or `post`
     * @param fieldset Form fields
     * @param enctype how the form-data should be encoded when submitting it to the server
     */
    public Form(@NonNull String action,
                @NonNull String method,
                @NonNull Fieldset fieldset,
                @Nullable String enctype) {
        this(action, method, fieldset, enctype, null);
    }

    /**
     *
     * @param action Form Action
     * @param method Form Method. either `get` or `post`
     * @param fieldset Form fields
     */
    public Form(@NonNull String action,
                @NonNull String method,
                @NonNull Fieldset fieldset) {
        this(action, method, fieldset, null, null);
    }

    /**
     *
     * @param action Form Action
     * @param fieldset Form fields
     */
    public Form(@NonNull String action,
                @NonNull Fieldset fieldset) {
        this(action, POST, fieldset, null, null);
    }

    /**
     *
     * @param action Form Action
     * @param fieldset Form fields
     * @param enctype how the form-data should be encoded when submitting it to the server
     */
    public Form(@NonNull String action,
                @NonNull Fieldset fieldset,
                @Nullable String enctype) {
        this(action, POST, fieldset, enctype, null);
    }
}
