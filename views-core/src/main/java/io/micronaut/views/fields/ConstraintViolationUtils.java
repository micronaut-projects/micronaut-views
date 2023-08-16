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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ElementKind;
import jakarta.validation.Path;

import java.util.Iterator;
import java.util.Optional;

@Internal
public final class ConstraintViolationUtils {

    private ConstraintViolationUtils() {

    }

    @NonNull
    public static Optional<String> lastNode(@NonNull ConstraintViolation<?> violation) {
        Path propertyPath = violation.getPropertyPath();
        Iterator<Path.Node> i = propertyPath.iterator();
        String lastNode = "";
        while (i.hasNext()) {
            Path.Node node = i.next();
            if (node.getKind() == ElementKind.METHOD || node.getKind() == ElementKind.CONSTRUCTOR) {
                continue;
            }
            if (node.getKind() != ElementKind.CONTAINER_ELEMENT) {
                lastNode = node.getName();
            }
        }
        return lastNode != null ?
            Optional.of(lastNode) :
            Optional.empty();
    }

    @NonNull
    public static Optional<String> constraintCode(@NonNull ConstraintViolation<?> violation) {
        String messageTemplate = violation.getMessageTemplate();
        int index = messageTemplate.lastIndexOf(".message}");
        if (index != -1) {
            String constraintType = messageTemplate.substring(0, index);
            constraintType = constraintType.substring(constraintType.lastIndexOf(".") + ".".length());
            return Optional.of(constraintType);
        }
        return Optional.empty();
    }

}
