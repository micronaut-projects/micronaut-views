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

import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputPassword;
import io.micronaut.views.fields.annotations.InputTel;
import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class DefaultFieldGenerator implements FieldsetGenerator {

    private static final String BUILDER_METHOD_TYPE = "type";
    private static final String BUILDER_METHOD_NAME = "name";
    private static final String BUILDER_METHOD_REQUIRED = "required";
    private static final String BUILDER_METHOD_VALUE = "value";
    private static final String BUILDER_METHOD_ERRORS = "errors";
    private static final String BUILDER_METHOD_ID = "id";
    private static final String BUILDER_METHOD_LABEL = "label";
    private static final String MICRONAUT_DATA_ANNOTATION_ID = "io.micronaut.data.annotation.Id";
    private static final String BUILDER_METHOD_CHECKBOXES = "checkboxes";
    private static final String BUILDER_METHOD_CHECKED = "checked";
    private static final String BUILDER_METHOD_OPTIONS = "options";
    private static final String MEMBER_FETCHER = "fetcher";

    private final EnumOptionFetcher enumOptionFetcher;
    private final BeanContext beanContext;

    private final ConcurrentHashMap<Class<? extends OptionFetcher>, OptionFetcher> optionFetcherCache = new ConcurrentHashMap<>();

    public DefaultFieldGenerator(EnumOptionFetcher enumOptionFetcher, BeanContext beanContext) {
        this.enumOptionFetcher = enumOptionFetcher;
        this.beanContext = beanContext;
    }

    @Override
    @NonNull
    public <T> Fieldset generate(@NonNull Class<T> type) {
        BeanIntrospection<T> introspection = BeanIntrospection.getIntrospection(type);
        return new Fieldset(formElements(introspection.getBeanProperties()), Collections.emptyList());
    }

    @Override
    public Fieldset generate(@NonNull Object instance) {
        return new Fieldset(generate(BeanWrapper.getWrapper(instance), null), Collections.emptyList());
    }

    @Override
    public Fieldset generate(@NonNull Object instance, @NonNull ConstraintViolationException ex) {
        List<? extends FormElement> fields = generate(BeanWrapper.getWrapper(instance), ex);
        List<Message> errors = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            if (ConstraintViolationUtils.lastNode(constraintViolation).isEmpty()) {
                errors.add(new ConstraintViolationMessage(constraintViolation));
            }
        }
        return new Fieldset(fields, errors);
    }

    @NonNull
    private <T> List<? extends FormElement> generate(@NonNull BeanWrapper<T> beanWrapper, @Nullable ConstraintViolationException ex) {
        List<FormElement> result = new ArrayList<>();
        for (BeanProperty<T, ?> beanProperty : beanWrapper.getBeanProperties()) {
            Class<? extends FormElement> formElementClazz = formElementClassForBeanProperty(beanProperty);
            BeanIntrospection.Builder<? extends FormElement> builder = formElementBuilderForBeanProperty(beanProperty, formElementClazz, beanWrapper, ex);
            result.add(builder.build());
        }
        return result;
    }

    private <T> Class<? extends FormElement> formElementClassForBeanProperty(BeanProperty<T, ?> beanProperty) {
        if (beanProperty.hasAnnotation(InputPassword.class)) {
            return InputPasswordFormElement.class;
        }
        if (beanProperty.hasAnnotation(InputEmail.class)) {
            return InputEmailFormElement.class;
        }
        if (beanProperty.hasAnnotation(InputTel.class)) {
            return InputTelFormElement.class;
        }
        if (beanProperty.hasAnnotation(Select.class)) {
            return SelectFormElement.class;
        }

        if (beanProperty.getType() == LocalDateTime.class) {
            return InputDataTimeLocalFormElement.class;
        }

        if (beanProperty.getType() == boolean.class) {
            return InputCheckboxFormElement.class;
        }

        if (beanProperty.getType().isEnum()) {
            return SelectFormElement.class;
        }

        return InputTextFormElement.class;
    }

    private <T> Optional<OptionFetcher> optionFetcherForBeanProperty(BeanProperty<T, ?> beanProperty) {
        if (beanProperty.hasAnnotation(Select.class)) {
            AnnotationValue<Select> annotation = beanProperty.getAnnotation(Select.class);
            Optional<Class<?>> optionFetcherClassOptional = annotation.classValue(MEMBER_FETCHER);
            if (optionFetcherClassOptional.isPresent()) {
                Class<? extends OptionFetcher> optionFetcherClass = (Class<? extends OptionFetcher>) optionFetcherClassOptional.get();
                if (optionFetcherCache.containsKey(optionFetcherClass)) {
                    return Optional.of(optionFetcherCache.get(optionFetcherClass));
                }
                Optional<?> optionFetcherOptional = beanContext.findBean(optionFetcherClass);
                if (optionFetcherOptional.isPresent()) {
                    OptionFetcher optionFetcher = (OptionFetcher) optionFetcherOptional.get();
                    optionFetcherCache.putIfAbsent(optionFetcherClass, optionFetcher);
                    return Optional.of(optionFetcher);
                }
            }
        }
        if (beanProperty.getType().isEnum()) {
            return Optional.of(enumOptionFetcher);
        }
        return Optional.empty();
    }

    private <T> BeanIntrospection.Builder<? extends FormElement> formElementBuilderForBeanProperty(BeanProperty<T, ?> beanProperty,
                                                                                                   Class<? extends FormElement> formElementClazz,
                                                                                                   @Nullable BeanWrapper<T> beanWrapper,
                                                                                                   @Nullable ConstraintViolationException ex) {
        BeanIntrospection.Builder<? extends FormElement> builder = BeanIntrospection.getIntrospection(formElementClazz).builder();
        if (formElementClazz == InputCheckboxFormElement.class) {
            FormElement formElement = formElementBuilderForBeanProperty(beanProperty, Checkbox.class, beanWrapper, ex)
                    .with(BUILDER_METHOD_CHECKED, valueForBeanProperty(beanWrapper, beanProperty).orElse(false))
                    .build();
            builder.with(BUILDER_METHOD_CHECKBOXES, Collections.singletonList(formElement));
        } else if (formElementClazz == SelectFormElement.class) {
            optionFetcherForBeanProperty(beanProperty)
                    .ifPresent(optionFetcher -> {
                        Optional<Object> valueOptional = valueForBeanProperty(beanWrapper, beanProperty);
                        if (valueOptional.isPresent()) {
                            builder.with(BUILDER_METHOD_OPTIONS, optionFetcher.generate(valueOptional.get()));
                        } else {
                            builder.with(BUILDER_METHOD_OPTIONS, optionFetcher.generate(beanProperty.getType()));
                        }
                    });
        }

        builder
            .with(BUILDER_METHOD_NAME, beanProperty.getName())
            .with(BUILDER_METHOD_ID, idForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_LABEL, labelForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_REQUIRED, requiredForBeanProperty(beanProperty));

        valueForBeanProperty(beanWrapper, beanProperty)
                .ifPresent(value -> builder.with(BUILDER_METHOD_VALUE, value.toString()));

        if (beanWrapper != null) {
            builder.with(BUILDER_METHOD_ERRORS, messagesForBeanProperty(beanProperty, ex));
        }
        return builder;
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

    private <T> List<? extends FormElement> formElements(Collection<BeanProperty<T, Object>> beanProperties) {
        return beanProperties.stream().map(this::formElementOfBeanProperty).toList();
    }

    private FormElement formElementOfBeanProperty(BeanProperty<?, ?> beanProperty) {
        Class<? extends FormElement> formElementClass = formElementClassForBeanProperty(beanProperty);
        BeanIntrospection.Builder<? extends FormElement> builder = formElementBuilderForBeanProperty(beanProperty, formElementClass, null, null);
        return builder.build();
    }


    private <T> Optional<Object> valueForBeanProperty(@Nullable BeanWrapper<T> beanWrapper, BeanProperty<T, ?> beanProperty) {
        if (beanWrapper != null) {
            return Optional.ofNullable(beanProperty.get(beanWrapper.getBean()));
        }

        if (beanProperty.getType() == boolean.class) {
            return Optional.of(false);
        }

        return Optional.empty();
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
