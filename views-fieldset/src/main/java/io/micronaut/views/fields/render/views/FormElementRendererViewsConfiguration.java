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
package io.micronaut.views.fields.render.views;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.render.FormElementRenderer;
import io.micronaut.views.fields.render.FormElementRendererConfigurationProperties;

/**
 * Configuration for {@link FormElementRenderer} based on views.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param inputCheckbox The view name for a field of type input checkbox.
 * @param inputDate The view name for a field of type input date.
 * @param inputDateTimeLocal The view name for a field of type input datetime-local.
 * @param inputEmail The view name for a field of type input email.
 * @param inputHidden The view name for a field of type input hidden.
 * @param inputNumber The view name for a field of type input number.
 * @param inputPassword The view name for a field of type input password.
 * @param inputRadio The view name for a field of type input radio.
 * @param inputSubmit The view name for a field of type input submit.
 * @param inputTel The view name for a field of type input tel.
 * @param inputText The view name for a field of type input text.
 * @param inputTime The view name for a field of type input time.
 * @param inputUrl The view name for a field of type input url.
 * @param option The view name to render an option html element.
 * @param select The view name for a field of type select.
 * @param textarea The view name to render a textarea html element.
 * @param trixEditor The view name for a field of type trix-editor.
 */
@ConfigurationProperties(FormElementRendererViewsConfiguration.PREFIX)
public record FormElementRendererViewsConfiguration(@Nullable String inputCheckbox,
                                                    @Nullable String inputDate,
                                                    @Nullable String inputDateTimeLocal,
                                                    @Nullable String inputEmail,
                                                    @Nullable String inputHidden,
                                                    @Nullable String inputNumber,
                                                    @Nullable String inputPassword,
                                                    @Nullable String inputRadio,
                                                    @Nullable String inputSubmit,
                                                    @Nullable String inputTel,
                                                    @Nullable String inputText,
                                                    @Nullable String inputTime,
                                                    @Nullable String inputUrl,
                                                    @Nullable String option,
                                                    @Nullable String select,
                                                    @Nullable String textarea,
                                                    @Nullable String trixEditor) {
    /**
     * Configuration Prefix.
     */
    public static final String PREFIX = FormElementRendererConfigurationProperties.PREFIX + ".views";
}
