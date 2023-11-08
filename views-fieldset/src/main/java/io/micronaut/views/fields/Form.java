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

/**
 * Representation of an HTML form.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param action Form Action
 * @param method Form Method. For example `post`
 * @param fieldset Form fields
 */
@Experimental
@Introspected
public record Form(String action, String method, Fieldset fieldset) {
}
