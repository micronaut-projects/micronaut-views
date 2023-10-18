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
package io.micronaut.views.fields.annotations;

import io.micronaut.views.fields.CheckboxFetcher;
import io.micronaut.views.fields.EnumCheckboxFetcher;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to specify a field is a checkbox input.
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/checkbox">Input Checkbox</a>
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.FIELD})
public @interface InputCheckbox {
    /**
     *
     * @return A checkbox fetcher
     */
    Class<? extends CheckboxFetcher> fetcher() default EnumCheckboxFetcher.class;
}
