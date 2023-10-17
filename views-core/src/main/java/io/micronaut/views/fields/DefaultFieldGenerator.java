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
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.views.fields.annotations.*;
import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

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
    public static final String BUILDER_METHOD_BUTTONS = "buttons";
    public static final String BULDER_METHOD_MIN = "min";

    private final EnumOptionFetcher<?> enumOptionFetcher;

    private final EnumRadioFetcher<?> enumRadioFetcher;
    private final BeanContext beanContext;

    private final ConcurrentHashMap<Class<? extends OptionFetcher>, OptionFetcher> optionFetcherCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends RadioFetcher>, RadioFetcher> radioFetcherCache = new ConcurrentHashMap<>();

    public DefaultFieldGenerator(EnumOptionFetcher<?> enumOptionFetcher,
                                 EnumRadioFetcher<?> enumRadioFetcher,
                                 BeanContext beanContext) {
        this.enumOptionFetcher = enumOptionFetcher;
        this.enumRadioFetcher = enumRadioFetcher;
        this.beanContext = beanContext;
    }

    @Override
    @NonNull
    public <T> Fieldset generate(@NonNull Class<T> type) {
        BeanIntrospection<T> introspection = BeanIntrospection.getIntrospection(type);
        return new Fieldset(formElements(introspection.getBeanProperties(), null), Collections.emptyList());
    }

    @Override
    public <T> Fieldset generate(@NonNull Class<T> type,
                                 @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        BeanIntrospection<T> introspection = BeanIntrospection.getIntrospection(type);
        return new Fieldset(formElements(introspection.getBeanProperties(), builderConsumer), Collections.emptyList());
    }

    @Override
    public Fieldset generate(@NonNull Object instance) {
        return new Fieldset(generateOfBeanWrapper(BeanWrapper.getWrapper(instance), null, null), Collections.emptyList());
    }

    @Override
    @NonNull
    public Fieldset generate(@NonNull Object instance,
                             @NonNull BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return new Fieldset(generateOfBeanWrapper(BeanWrapper.getWrapper(instance), null, builderConsumer), Collections.emptyList());
    }

    @Override
    public Fieldset generate(@NonNull Object instance, @NonNull ConstraintViolationException ex) {
        return generate(generateOfBeanWrapper(BeanWrapper.getWrapper(instance), ex, null), ex);
    }

    @Override
    public Fieldset generate(Object instance, ConstraintViolationException ex, BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return generate(generateOfBeanWrapper(BeanWrapper.getWrapper(instance), ex, builderConsumer), ex);
    }

    @NonNull
    private Fieldset generate(@NonNull List<? extends FormElement> fields, @NonNull ConstraintViolationException ex) {
        List<Message> errors = new ArrayList<>();
        for (ConstraintViolation<?> constraintViolation : ex.getConstraintViolations()) {
            if (ConstraintViolationUtils.lastNode(constraintViolation).isEmpty()) {
                errors.add(new ConstraintViolationMessage(constraintViolation));
            }
        }
        return new Fieldset(fields, errors);
    }

    @NonNull
    private <T> List<? extends FormElement> generateOfBeanWrapper(@NonNull BeanWrapper<T> beanWrapper,
                                                                  @Nullable ConstraintViolationException ex,
                                                                  @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        List<FormElement> result = new ArrayList<>();
        for (BeanProperty<T, ?> beanProperty : beanWrapper.getBeanProperties()) {
            formElementClassForBeanProperty(beanProperty).ifPresent(formElementClazz  -> {
                BeanIntrospection.Builder<? extends FormElement> builder = formElementBuilderForBeanProperty(beanProperty, formElementClazz, beanWrapper, ex, builderConsumer);
                result.add(builder.build());
            });
        }
        return result;
    }

    @NonNull
    private <T> Optional<Class<? extends FormElement>> formElementClassForBeanProperty(@NonNull BeanProperty<T, ?> beanProperty) {
        if (beanProperty.hasStereotype(AutoPopulated.class)) {
            return Optional.empty();
        }
        if (beanProperty.hasAnnotation(InputHidden.class)) {
            return Optional.of(InputHiddenFormElement.class);
        }
        if (beanProperty.hasAnnotation(InputRadio.class)) {
            return Optional.of(InputRadioFormElement.class);
        }
        if (beanProperty.hasAnnotation(InputPassword.class)) {
            return Optional.of(InputPasswordFormElement.class);
        }
        if (beanProperty.hasAnnotation(InputEmail.class)) {
            return Optional.of(InputEmailFormElement.class);
        }
        if (beanProperty.hasAnnotation(InputUrl.class)) {
            return Optional.of(InputUrlFormElement.class);
        }
        if (beanProperty.hasAnnotation(InputTel.class)) {
            return Optional.of(InputTelFormElement.class);
        }
        if (beanProperty.hasAnnotation(Select.class)) {
            return Optional.of(SelectFormElement.class);
        }
        if (beanProperty.hasAnnotation(Textarea.class)) {
            return Optional.of(TextareaFormElement.class);
        }
        if (beanProperty.getType() == LocalDate.class) {
            return Optional.of(InputDateFormElement.class);
        }
        if (beanProperty.getType() == LocalDateTime.class) {
            return Optional.of(InputDataTimeLocalFormElement.class);
        }

        if (Number.class.isAssignableFrom(beanProperty.getType())) {
            return Optional.of(InputNumberFormElement.class);
        }

        if (beanProperty.getType() == boolean.class) {
            return Optional.of(InputCheckboxFormElement.class);
        }

        if (beanProperty.getType().isEnum()) {
            return Optional.of(SelectFormElement.class);
        }

        if (CharSequence.class.isAssignableFrom(beanProperty.getType())) {
            return Optional.of(InputTextFormElement.class);
        }

        return Optional.empty();
    }

    @NonNull
    private <T> Optional<RadioFetcher> radioFetcherForBeanProperty(@NonNull BeanProperty<T, ?> beanProperty) {
        if (beanProperty.hasAnnotation(InputRadio.class)) {
            AnnotationValue<InputRadio> annotation = beanProperty.getAnnotation(InputRadio.class);
            Optional<Class<?>> radioFetcherClassOptional = annotation.classValue(MEMBER_FETCHER);
            if (radioFetcherClassOptional.isPresent()) {
                Class<? extends RadioFetcher> radioFetcherClass = (Class<? extends RadioFetcher>) radioFetcherClassOptional.get();
                if (radioFetcherCache.containsKey(radioFetcherClass)) {
                    return Optional.of(radioFetcherCache.get(radioFetcherClass));
                }
                Optional<?> radioFetcherOptional = beanContext.findBean(radioFetcherClass);
                if (radioFetcherOptional.isPresent()) {
                    RadioFetcher radioFetcher = (RadioFetcher) radioFetcherOptional.get();
                    radioFetcherCache.putIfAbsent(radioFetcherClass, radioFetcher);
                    return Optional.of(radioFetcher);
                }
            }
        }
        if (beanProperty.getType().isEnum()) {
            return Optional.of(enumRadioFetcher);
        }
        return Optional.empty();
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
                                                                                                   @Nullable ConstraintViolationException ex,
                                                                                                   @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        BeanIntrospection.Builder<? extends FormElement> builder = BeanIntrospection.getIntrospection(formElementClazz).builder();
        if (formElementClazz == InputCheckboxFormElement.class) {
            FormElement formElement = formElementBuilderForBeanProperty(beanProperty, Checkbox.class, beanWrapper, ex, null)
                    .with(BUILDER_METHOD_CHECKED, valueForBeanProperty(beanWrapper, beanProperty).map(v -> Boolean.valueOf(v.toString())).orElse(false))
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
        } else if (formElementClazz == InputRadioFormElement.class) {
            radioFetcherForBeanProperty(beanProperty)
                    .ifPresent(fetcher -> {
                        Optional<Object> valueOptional = valueForBeanProperty(beanWrapper, beanProperty);
                        if (valueOptional.isPresent()) {
                            builder.with(BUILDER_METHOD_BUTTONS, fetcher.generate(valueOptional.get()));
                        } else {
                            builder.with(BUILDER_METHOD_BUTTONS, fetcher.generate(beanProperty.getType()));
                        }
                    });
        }
        builder
            .with(BUILDER_METHOD_NAME, beanProperty.getName())
            .with(BUILDER_METHOD_ID, idForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_LABEL, labelForBeanProperty(beanProperty))
            .with(BUILDER_METHOD_REQUIRED, requiredForBeanProperty(beanProperty));
        valueForBeanProperty(beanWrapper, beanProperty)
                .ifPresent(value -> {
                    try {
                        builder.with(BUILDER_METHOD_VALUE, value);
                    } catch (IllegalArgumentException e) {
                        builder.with(BUILDER_METHOD_VALUE, value.toString());
                    }
                });

        if (beanWrapper != null) {
            builder.with(BUILDER_METHOD_ERRORS, messagesForBeanProperty(beanProperty, ex));
        }
        minForBeanProperty(beanProperty).ifPresent(min -> builder.with(BULDER_METHOD_MIN, min));
        if (builderConsumer != null) {
            builderConsumer.accept(beanProperty.getName(), builder);
        }
        return builder;
    }

    private Optional<Object> minForBeanProperty(BeanProperty<?, ?> beanProperty) {
        if (beanProperty.hasAnnotation(Positive.class)) {
            return Optional.of(1);
        }
        return Optional.empty();
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

    private <T> List<? extends FormElement> formElements(Collection<BeanProperty<T, Object>> beanProperties, @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return beanProperties.stream().map(beanProperty -> formElementOfBeanProperty(beanProperty, builderConsumer)).filter(Objects::nonNull).toList();
    }

    @Nullable
    private FormElement formElementOfBeanProperty(@NonNull BeanProperty<?, ?> beanProperty, @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return formElementClassForBeanProperty(beanProperty)
                .map(formElementClass -> formElementBuilderForBeanProperty(beanProperty, formElementClass, null, null, builderConsumer).build())
                        .orElse(null);
    }


    private <T> Optional<Object> valueForBeanProperty(@Nullable BeanWrapper<T> beanWrapper, BeanProperty<T, ?> beanProperty) {
        if (beanWrapper != null) {
            Object value = beanProperty.get(beanWrapper.getBean());
            if (beanProperty.getType() == boolean.class) {
                return Optional.of(((boolean) value) ? StringUtils.TRUE : StringUtils.FALSE);
            }
            return Optional.ofNullable(value);
        }
        if (beanProperty.getType() == boolean.class) {
            return Optional.of(StringUtils.FALSE);
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
