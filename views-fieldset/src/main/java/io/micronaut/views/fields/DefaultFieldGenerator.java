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
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.annotation.AutoPopulated;
import io.micronaut.views.fields.annotations.InputCheckbox;
import io.micronaut.views.fields.annotations.InputEmail;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.InputPassword;
import io.micronaut.views.fields.annotations.InputRadio;
import io.micronaut.views.fields.annotations.InputTel;
import io.micronaut.views.fields.annotations.InputUrl;
import io.micronaut.views.fields.annotations.Select;
import io.micronaut.views.fields.annotations.Textarea;
import io.micronaut.views.fields.annotations.TrixEditor;
import io.micronaut.views.fields.elements.*;
import io.micronaut.views.fields.fetchers.*;
import io.micronaut.views.fields.messages.ConstraintViolationMessage;
import io.micronaut.views.fields.messages.ConstraintViolationUtils;
import io.micronaut.views.fields.messages.Message;
import jakarta.annotation.Nonnull;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.*;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Default implementation of {@link FieldsetGenerator} which relies on the types being introspected.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Singleton
public class DefaultFieldGenerator implements FieldsetGenerator {
    private static final String BUILDER_METHOD_NAME = "name";
    private static final String BUILDER_METHOD_REQUIRED = "required";
    private static final String BUILDER_METHOD_VALUE = "value";
    private static final String BUILDER_METHOD_ERRORS = "errors";
    private static final String BUILDER_METHOD_ID = "id";
    private static final String BUILDER_METHOD_LABEL = "label";
    private static final String BUILDER_METHOD_CHECKBOXES = "checkboxes";
    private static final String BUILDER_METHOD_CHECKED = "checked";
    private static final String BUILDER_METHOD_OPTIONS = "options";
    private static final String MEMBER_FETCHER = "fetcher";
    private static final String BUILDER_METHOD_BUTTONS = "buttons";
    private static final String BUILDER_METHOD_MIN = "min";
    private static final String BUILDER_METHOD_MAX = "max";
    private static final String BUILDER_METHOD_MAX_LENGTH = "maxLength";
    private static final String BUILDER_METHOD_MIN_LENGTH = "minLength";

    private static final Map<Class<? extends Annotation>, Class<? extends FormElement>> ANNOTATION_MAPPING = Map.ofEntries(
        Map.entry(InputHidden.class, InputHiddenFormElement.class),
        Map.entry(InputRadio.class, InputRadioFormElement.class),
        Map.entry(InputCheckbox.class, InputCheckboxFormElement.class),
        Map.entry(InputPassword.class, InputPasswordFormElement.class),
        Map.entry(InputEmail.class, InputEmailFormElement.class),
        Map.entry(Email.class, InputEmailFormElement.class),
        Map.entry(InputUrl.class, InputUrlFormElement.class),
        Map.entry(InputTel.class, InputTelFormElement.class),
        Map.entry(Select.class, SelectFormElement.class),
        Map.entry(Textarea.class, TextareaFormElement.class),
        Map.entry(TrixEditor.class, TrixEditorFormElement.class)
    );
    private static final String MEMBER_MIN = "min";
    private static final String MEMBER_MAX = "max";

    private final EnumOptionFetcher<?> enumOptionFetcher;

    private final EnumRadioFetcher<?> enumRadioFetcher;

    private final EnumCheckboxFetcher<?> enumCheckboxFetcher;
    private final BeanContext beanContext;

    private final ConcurrentHashMap<Class<? extends OptionFetcher>, OptionFetcher> optionFetcherCache = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<? extends RadioFetcher>, RadioFetcher> radioFetcherCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Class<? extends CheckboxFetcher>, CheckboxFetcher> checkboxFetcherCache = new ConcurrentHashMap<>();

    /**
     * @param enumOptionFetcher   Enum fetcher for {@link Option}.
     * @param enumRadioFetcher    Enum fetcher for {@link Radio}.
     * @param enumCheckboxFetcher Enum fetcher for {@link Checkbox}.
     * @param beanContext         Bean Context
     */
    public DefaultFieldGenerator(EnumOptionFetcher<?> enumOptionFetcher,
                                 EnumRadioFetcher<?> enumRadioFetcher,
                                 EnumCheckboxFetcher<?> enumCheckboxFetcher,
                                 BeanContext beanContext) {
        this.enumOptionFetcher = enumOptionFetcher;
        this.enumRadioFetcher = enumRadioFetcher;
        this.enumCheckboxFetcher = enumCheckboxFetcher;
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
        return new Fieldset(fields, ex.getConstraintViolations()
                .stream()
                .filter(constraintViolationEx -> ConstraintViolationUtils.lastNode(constraintViolationEx).isEmpty())
                .map(ConstraintViolationMessage::new)
                .map(Message.class::cast)
                .sorted()
                .toList());
    }

    @NonNull
    private <T> List<? extends FormElement> generateOfBeanWrapper(@NonNull BeanWrapper<T> beanWrapper,
                                                                  @Nullable ConstraintViolationException ex,
                                                                  @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        List<FormElement> result = new ArrayList<>();
        for (BeanProperty<T, ?> beanProperty : beanWrapper.getBeanProperties()) {
            formElementClassForBeanProperty(beanProperty).ifPresent(formElementClazz -> {
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
        for (var mapping : ANNOTATION_MAPPING.entrySet()) {
            if (beanProperty.hasAnnotation(mapping.getKey())) {
                return Optional.of(mapping.getValue());
            }
        }
        if (beanProperty.getType() == LocalDate.class) {
            return Optional.of(InputDateFormElement.class);
        }
        if (beanProperty.getType() == LocalDateTime.class) {
            return Optional.of(InputDateTimeLocalFormElement.class);
        }
        if (beanProperty.getType() == LocalTime.class) {
            return Optional.of(InputTimeFormElement.class);
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

    @NonNull
    private <T> Optional<CheckboxFetcher> checkboxFetcherForBeanProperty(@NonNull BeanProperty<T, ?> beanProperty) {
        if (beanProperty.hasAnnotation(InputCheckbox.class)) {
            AnnotationValue<InputCheckbox> annotation = beanProperty.getAnnotation(InputCheckbox.class);
            Optional<Class<?>> fetcherClassOptional = annotation.classValue(MEMBER_FETCHER);
            if (fetcherClassOptional.isPresent()) {
                Class<? extends CheckboxFetcher> fetcherClass = (Class<? extends CheckboxFetcher>) fetcherClassOptional.get();
                if (checkboxFetcherCache.containsKey(fetcherClass)) {
                    return Optional.of(checkboxFetcherCache.get(fetcherClass));
                }
                Optional<?> fetcherOptional = beanContext.findBean(fetcherClass);
                if (fetcherOptional.isPresent()) {
                    CheckboxFetcher fetcher = (CheckboxFetcher) fetcherOptional.get();
                    checkboxFetcherCache.putIfAbsent(fetcherClass, fetcher);
                    return Optional.of(fetcher);
                }
            }
        }
        if (beanProperty.getType().isEnum()) {
            return Optional.of(enumCheckboxFetcher);
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

    private <T, E extends FormElement> BeanIntrospection.Builder<E> formElementBuilderForBeanProperty(BeanProperty<T, ?> beanProperty,
                                                                                                      Class<E> formElementClazz,
                                                                                                      @Nullable BeanWrapper<T> beanWrapper,
                                                                                                      @Nullable ConstraintViolationException ex,
                                                                                                      @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        BeanIntrospection.Builder<E> builder = BeanIntrospection.getIntrospection(formElementClazz).builder();

        if (formElementClazz == InputCheckboxFormElement.class) {
            builder.with(
                BUILDER_METHOD_CHECKBOXES,
                checkboxFetcherForBeanProperty(beanProperty)
                    .map(fetcher ->
                        valueForBeanProperty(beanWrapper, beanProperty)
                            .map(fetcher::generate)
                            .orElseGet(() -> fetcher.generate(beanProperty.getType()))
                    ).orElseGet(() -> {
                        Checkbox formElement = formElementBuilderForBeanProperty(beanProperty, Checkbox.class, beanWrapper, ex, null)
                            .with(BUILDER_METHOD_CHECKED, valueForBeanProperty(beanWrapper, beanProperty).map(v -> Boolean.valueOf(v.toString())).orElse(false))
                            .build();
                        return Collections.singletonList(formElement);
                    })
            );
        } else if (formElementClazz == SelectFormElement.class) {
            builder.with(BUILDER_METHOD_OPTIONS, optionFetcherForBeanProperty(beanProperty)
                .map(optionFetcher ->
                    valueForBeanProperty(beanWrapper, beanProperty)
                        .map(optionFetcher::generate)
                        .orElseGet(() -> optionFetcher.generate(beanProperty.getType()))
                )
                .orElse(Collections.emptyList())
            );
        } else if (formElementClazz == InputRadioFormElement.class) {
            builder.with(BUILDER_METHOD_BUTTONS, radioFetcherForBeanProperty(beanProperty)
                .map(fetcher ->
                    valueForBeanProperty(beanWrapper, beanProperty)
                        .map(fetcher::generate)
                        .orElseGet(() -> fetcher.generate(beanProperty.getType()))
                )
                .orElse(Collections.emptyList())
            );
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
        minForBeanProperty(beanProperty).ifPresent(min -> builder.with(BUILDER_METHOD_MIN, min));
        maxForBeanProperty(beanProperty).ifPresent(max -> builder.with(BUILDER_METHOD_MAX, max));
        minLengthForBeanProperty(beanProperty).ifPresent(min -> builder.with(BUILDER_METHOD_MIN_LENGTH, min));
        maxLengthForBeanProperty(beanProperty).ifPresent(max -> builder.with(BUILDER_METHOD_MAX_LENGTH, max));
        if (builderConsumer != null) {
            builderConsumer.accept(beanProperty.getName(), builder);
        }
        return builder;
    }

    /**
     * Returns a max value for the bean property.
     * For example, if the bean property is annotated with {@link Max} the value of the Max annotation is returned by this method.
     * @param beanProperty The Bean Property
     * @return max value if any.
     */
    private Optional<Object> maxForBeanProperty(BeanProperty<?, ?> beanProperty) {
        return annotationValueForBeanProperty(beanProperty, Max.class);
    }

    private Optional<Object> minForBeanProperty(BeanProperty<?, ?> beanProperty) {
        if (beanProperty.hasAnnotation(Positive.class)) {
            return Optional.of(1);
        }
        return annotationValueForBeanProperty(beanProperty, Min.class);
    }

    /**
     * Returns a  min Length value for the bean property.
     * For example, if the bean property is annotated with {@link Size} the value of the member {@link Size#min()} is returned by this method.
     * @param beanProperty The Bean Property
     * @return min length value if any.
     */
    private Optional<Object> minLengthForBeanProperty(BeanProperty<?, ?> beanProperty) {
        return annotationValueForBeanProperty(beanProperty, Size.class, MEMBER_MIN);
    }

    /**
     * Returns a  max Length value for the bean property.
     * For example, if the bean property is annotated with {@link Size} the value of the member {@link Size#max()} is returned by this method.
     * @param beanProperty The Bean Property
     * @return max length value if any.
     */
    private Optional<Object> maxLengthForBeanProperty(BeanProperty<?, ?> beanProperty) {
        return annotationValueForBeanProperty(beanProperty, Size.class, MEMBER_MAX);
    }

    private Message labelForBeanProperty(BeanProperty<?, ?> beanProperty) {
        String code = beanProperty.getDeclaringBean().getBeanType().getSimpleName().toLowerCase() + "." + beanProperty.getName();
        String defaultMessage = StringUtils.capitalize(beanProperty.getName().replaceAll("(.)([A-Z])", "$1 $2"));
        return Message.of(defaultMessage, code);
    }

    private List<ConstraintViolationMessage> messagesForBeanProperty(BeanProperty<?, ?> beanProperty, @Nullable ConstraintViolationException ex) {
        if (ex != null) {
            return ex.getConstraintViolations().stream()
                .filter(violation -> {
                    Optional<String> lastNodeOptional = ConstraintViolationUtils.lastNode(violation);
                    return lastNodeOptional.isPresent() && lastNodeOptional.get().equals(beanProperty.getName());
                }).map(ConstraintViolationMessage::new)
                    .sorted()
                    .toList();
        }
        return Collections.emptyList();
    }

    private <T> List<? extends FormElement> formElements(Collection<BeanProperty<T, Object>> beanProperties, @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return beanProperties.stream().flatMap(beanProperty -> formElementOfBeanProperty(beanProperty, builderConsumer).stream()).toList();
    }

    @Nullable
    private Optional<? extends FormElement> formElementOfBeanProperty(@NonNull BeanProperty<?, ?> beanProperty, @Nullable BiConsumer<String, BeanIntrospection.Builder<? extends FormElement>> builderConsumer) {
        return formElementClassForBeanProperty(beanProperty)
            .map(formElementClass -> formElementBuilderForBeanProperty(beanProperty, formElementClass, null, null, builderConsumer).build());
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

    private Optional<Object> annotationValueForBeanProperty(@NonNull BeanProperty<?, ?> beanProperty,
                                                            @NonNull Class<? extends Annotation> annotation) {
        if (beanProperty.hasAnnotation(annotation)) {
            AnnotationValue<?> ann = beanProperty.getAnnotation(annotation);
            OptionalLong optionalLong = ann.longValue();
            if (optionalLong.isPresent()) {
                return Optional.of(optionalLong.getAsLong());
            }
        }
        return Optional.empty();
    }

    private Optional<Object> annotationValueForBeanProperty(@NonNull BeanProperty<?, ?> beanProperty,
                                                            @NonNull Class<? extends Annotation> annotation,
                                                            @NonNull String member) {
        if (beanProperty.hasAnnotation(annotation)) {
            AnnotationValue<?> ann = beanProperty.getAnnotation(annotation);
            OptionalInt optionalInt = ann.intValue(member);
            if (optionalInt.isPresent()) {
                return Optional.of(optionalInt.getAsInt());
            }
        }
        return Optional.empty();
    }
}
