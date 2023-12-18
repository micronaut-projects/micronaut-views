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
package io.micronaut.views.fields.constraints;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import io.micronaut.views.fields.Form;

/**
 * Validator for the constraint {@link EnctypePostRequired} being applied to a {@link Form}.
 * @author Sergio del Amo
 * @since 5.1.0
 */
@Introspected
public class EnctypePostRequiredValidator implements ConstraintValidator<EnctypePostRequired, Form> {

    private static final String METHOD_POST = "post";

    @Override
    public boolean isValid(@Nullable Form form,
                           @NonNull AnnotationValue<EnctypePostRequired> annotationMetadata,
                           @NonNull ConstraintValidatorContext context) {
        if (form == null) {
            return true;
        }
        return form.enctype() == null || form.method().equals(METHOD_POST);
    }
}
