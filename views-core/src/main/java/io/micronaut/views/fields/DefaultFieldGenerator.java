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
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.core.util.StringUtils;
import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.*;

@Singleton
public class DefaultFieldGenerator implements FieldGenerator {

    public static final String BUILDER_METHOD_TYPE = "type";
    public static final String BUILDER_METHOD_NAME = "name";
    public static final String BUILDER_METHOD_REQUIRED = "required";
    public static final String BUILDER_METHOD_VALUE = "value";
    public static final String BUILDER_METHOD_ERRORS = "errors";
    public static final String BUILDER_METHOD_ID = "id";
    public static final String BUILDER_METHOD_LABEL = "label";
    public static final String MICRONAUT_DATA_ANNOTATION_ID = "io.micronaut.data.annotation.Id";

    @Override
    @NonNull
    public <T> Fieldset generate(@NonNull Class<T> type) {
        BeanIntrospection<T> introspection = BeanIntrospection.getIntrospection(type);
        return new Fieldset(getInputFields(introspection.getBeanProperties()), Collections.emptyList());
    }

    @Override
    public Fieldset generate(@NonNull Object instance) {
        return new Fieldset(generate(BeanWrapper.getWrapper(instance), null), Collections.emptyList());
    }

    @Override
    public Fieldset generate(@NonNull Object instance, @NonNull ConstraintViolationException ex) {
        List<InputField> fields = generate(BeanWrapper.getWrapper(instance), ex);
        List<Message> errors = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            if (ConstraintViolationUtils.lastNode(constraintViolation).isEmpty()) {
                errors.add(new ConstraintViolationMessage(constraintViolation));
            }
        }
        return new Fieldset(fields, errors);
    }

    @NonNull
    private <T> List<InputField> generate(@NonNull BeanWrapper<T> beanWrapper, @Nullable ConstraintViolationException ex) {
        List<InputField> result = new ArrayList<>();
        for (BeanProperty<T, ?> beanProperty : beanWrapper.getBeanProperties()) {
            BeanIntrospection.Builder<InputField> builder = inputFieldForBeanProperty(beanProperty);
            Object value = valueForBeanProperty(beanWrapper, beanProperty);
            if (value != null) {
                builder.with(BUILDER_METHOD_VALUE, value.toString());
            }
            builder.with(BUILDER_METHOD_ERRORS, messagesForBeanProperty(beanProperty, ex));
            result.add(builder.build());
        }
        return result;
    }

    private BeanIntrospection.Builder<InputField>  inputFieldForBeanProperty(BeanProperty<?, ?> beanProperty) {
        BeanIntrospection.Builder<InputField> builder = BeanIntrospection.getIntrospection(InputField.class).builder();
        return builder
            .with(BUILDER_METHOD_TYPE, inputTypeForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_NAME, beanProperty.getName())
            .with(BUILDER_METHOD_ID, idForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_LABEL, labelForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_REQUIRED, requiredForBeanProperty(beanProperty));
    }

    private Message labelForBeanProperty(BeanProperty<?, ?> beanProperty) {
        return Message.of(beanProperty.getDeclaringBean().getBeanType().getSimpleName().toLowerCase() + "." + beanProperty.getName(),
            StringUtils.capitalize(beanProperty.getName().replaceAll("(.)([A-Z])", "$1 $2")));
    }

    private List<ConstraintViolationMessage> messagesForBeanProperty(BeanProperty<?, ?> beanProperty, @Nullable ConstraintViolationException ex) {
        if (ex != null) {
            return ex.getConstraintViolations().stream()
                .filter(violation -> {
                    Optional<String> lastNodeOptional = ConstraintViolationUtils.lastNode(violation);
                    return lastNodeOptional.isPresent() && lastNodeOptional.get().equals(beanProperty.getName());
                }).map(ConstraintViolationMessage::new)
                .toList();
        }
        return Collections.emptyList();
    }

    private <T> List<InputField> getInputFields(Collection<BeanProperty<T, Object>> beanProperties) {
        List<InputField> result = new ArrayList<>();
        for (BeanProperty<?, ?> beanProperty : beanProperties) {
            InputField field = inputFieldForBeanProperty(beanProperty).build();
            if (field.getType() == InputType.HIDDEN) {
                continue;
            }
            result.add(field);
        }
        return result;
    }

    private <T> Object valueForBeanProperty(BeanWrapper<T> beanWrapper, BeanProperty<T, ?> beanProperty) {
        return beanProperty.get(beanWrapper.getBean());
    }

    private InputType inputTypeForBeanProperty(BeanProperty<?, ?> beanProperty) {
        if (beanProperty.hasAnnotation(MICRONAUT_DATA_ANNOTATION_ID)) {
            return InputType.HIDDEN;
        }
        if (beanProperty.hasAnnotation(Email.class)) {
            return InputType.EMAIL;
        }
        if (Number.class.isAssignableFrom(beanProperty.getType())) {
            return InputType.NUMBER;
        }

        if (beanProperty.getName().toLowerCase().contains("password")) {
            return InputType.PASSWORD;
        }

        return InputType.TEXT;
    }

    private boolean requiredForBeanProperty(BeanProperty<?, ?> beanProperty) {
        return beanProperty.hasAnnotation(Nonnull.class) ||
            beanProperty.hasAnnotation(NotNull.class) ||
            beanProperty.hasAnnotation(NotBlank.class) ||
            beanProperty.hasAnnotation(NonNull.class);
    }

    @NonNull
    private String idForBeanProperty(@NonNull BeanProperty<?, ?> beanProperty) {
        return beanProperty.getName();
    }
}
