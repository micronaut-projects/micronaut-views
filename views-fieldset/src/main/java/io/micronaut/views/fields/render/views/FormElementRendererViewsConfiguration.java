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

import io.micronaut.context.annotation.DefaultImplementation;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.render.FormElementRenderer;

/**
 * Configuration for {@link FormElementRenderer} based on views.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Experimental
@DefaultImplementation(FormElementRendererViewsConfigurationProperties.class)
public interface FormElementRendererViewsConfiguration {

    /**
     *
     * @return The view name for a field of type input checkbox.
     */
    @Nullable
    String getInputCheckbox();

    /**
     *
     * @return The view name for a field of type input date.
     */
    @Nullable
    String getInputDate();

    /**
     *
     * @return The view name for a field of type input datetime-local.
     */
    @Nullable
    String getInputDateTimeLocal();

    /**
     *
     * @return The view name for a field of type input email.
     */
    @Nullable
    String getInputEmail();

    /**
     *
     * @return The view name for a field of type input hidden.
     */
    @Nullable
    String getInputHidden();

    /**
     *
     * @return The view name for a field of type input number.
     */
    @Nullable
    String getInputNumber();

    /**
     *
     * @return The view name for a field of type input password.
     */
    @Nullable
    String getInputPassword();

    /**
     *
     * @return The view name for a field of type input radio.
     */
    @Nullable
    String getInputRadio();

    /**
     *
     * @return The view name for a field of type input submit.
     */
    @Nullable
    String getInputSubmit();

    /**
     *
     * @return The view name for a field of type input tel.
     */
    @Nullable
    String getInputTel();

    /**
     *
     * @return The view name for a field of type input text.
     */
    @Nullable
    String getInputText();

    /**
     *
     * @return The view name for a field of type input time.
     */
    @Nullable
    String getInputTime();

    /**
     *
     * @return The view name for a field of type input url.
     */
    @Nullable
    String getInputUrl();

    /**
     *
     * @return The view name to render an option html element.
     */
    @Nullable
    String getOption();

    /**
     *
     * @return The view name for a field of type select.
     */
    @Nullable
    String getSelect();

    /**
     *
     * @return The view name to render a textarea html element.
     */
    @Nullable
    String getTextarea();

    /**
     *
     * @return The view name for a field of type trix-editor.
     */
    @Nullable
    String getTrixEditor();
}
