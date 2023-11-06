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
package io.micronaut.views.fields.render;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link FormElementRendererConfiguration} based on a {@link ConfigurationProperties} instance.
 *
 * @author Sergio del Amo
 * @since 4.1.0
 */
@ConfigurationProperties(FormElementRendererConfigurationProperties.PREFIX)
public class FormElementRendererConfigurationProperties implements FormElementRendererConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".form-element-views";

    private String inputCheckbox;
    private String inputDate;
    private String inputDateTimeLocal;
    private String inputEmail;
    private String inputHidden;
    private String inputNumber;
    private String inputPassword;
    private String inputRadio;
    private String inputSubmit;
    private String inputTel;
    private String inputText;
    private String inputTime;
    private String inputUrl;
    private String option;
    private String select;
    private String textarea;
    private String trixEditor;

    @Override
    @Nullable
    public String getInputCheckbox() {
        return inputCheckbox;
    }

    /**
     * The view name for a field of type input checkbox.
     *
     * @param inputCheckbox The view name for a field of type input checkbox.
     */
    public void setInputCheckbox(@Nullable String inputCheckbox) {
        this.inputCheckbox = inputCheckbox;
    }

    @Override
    @Nullable
    public String getInputDate() {
        return inputDate;
    }

    /**
     * @param inputDate The view name for a field of type input date.
     */
    public void setInputDate(@Nullable String inputDate) {
        this.inputDate = inputDate;
    }

    @Override
    @Nullable
    public String getInputDateTimeLocal() {
        return inputDateTimeLocal;
    }

    /**
     * The view name for a field of type input datetime-local.
     *
     * @param inputDateTimeLocal The view name for a field of type input datetime-local.
     */
    public void setInputDateTimeLocal(@Nullable String inputDateTimeLocal) {
        this.inputDateTimeLocal = inputDateTimeLocal;
    }

    @Override
    @Nullable
    public String getInputEmail() {
        return inputEmail;
    }

    /**
     * The view name for a field of type input email.
     *
     * @param inputEmail The view name for a field of type input email.
     */
    public void setInputEmail(@Nullable String inputEmail) {
        this.inputEmail = inputEmail;
    }

    @Override
    @Nullable
    public String getInputHidden() {
        return inputHidden;
    }

    /**
     * The view name for a field of type input hidden.
     *
     * @param inputHidden The view name for a field of type input hidden.
     */
    public void setInputHidden(@Nullable String inputHidden) {
        this.inputHidden = inputHidden;
    }

    @Override
    @Nullable
    public String getInputNumber() {
        return inputNumber;
    }

    /**
     * @param inputNumber The view name for a field of type input number.
     */
    public void setInputNumber(@Nullable String inputNumber) {
        this.inputNumber = inputNumber;
    }

    @Override
    @Nullable
    public String getInputPassword() {
        return inputPassword;
    }

    /**
     * @param inputPassword The view name for a field of type input password.
     */
    public void setInputPassword(@Nullable String inputPassword) {
        this.inputPassword = inputPassword;
    }

    @Override
    @Nullable
    public String getInputRadio() {
        return inputRadio;
    }

    /**
     * @param inputRadio The view name for a field of type input radio.
     */
    public void setInputRadio(@Nullable String inputRadio) {
        this.inputRadio = inputRadio;
    }

    @Override
    @Nullable
    public String getInputSubmit() {
        return inputSubmit;
    }

    /**
     * The view name for a field of type input submit.
     *
     * @param inputSubmit The view name for a field of type input submit.
     */
    public void setInputSubmit(@Nullable String inputSubmit) {
        this.inputSubmit = inputSubmit;
    }

    @Override
    @Nullable
    public String getInputTel() {
        return inputTel;
    }

    /**
     * @param inputTel The view name for a field of type input tel.
     */
    public void setInputTel(@Nullable String inputTel) {
        this.inputTel = inputTel;
    }

    @Override
    @Nullable
    public String getInputText() {
        return inputText;
    }

    /**
     * @param inputText The view name for a field of type input text.
     */
    public void setInputText(@Nullable String inputText) {
        this.inputText = inputText;
    }

    @Override
    @Nullable
    public String getInputTime() {
        return inputTime;
    }

    /**
     * The view name for a field of type input time.
     *
     * @param inputTime The view name for a field of type input time.
     */
    public void setInputTime(@Nullable String inputTime) {
        this.inputTime = inputTime;
    }

    @Override
    @Nullable
    public String getInputUrl() {
        return inputUrl;
    }

    /**
     * The view name for a field of type input url.
     *
     * @param inputUrl The view name for a field of type input url.
     */
    public void setInputUrl(@Nullable String inputUrl) {
        this.inputUrl = inputUrl;
    }

    @Override
    @Nullable
    public String getSelect() {
        return select;
    }

    /**
     * The view name for a field of type select.
     *
     * @param select The view name for a field of type select.
     */
    public void setSelect(@Nullable String select) {
        this.select = select;
    }

    @Override
    @Nullable
    public String getOption() {
        return option;
    }

    /**
     * The view name to render an option html element.
     *
     * @param option The view name to render an option html element
     */
    public void setOption(@Nullable String option) {
        this.option = option;
    }

    @Override
    @Nullable
    public String getTextarea() {
        return textarea;
    }

    /**
     * The view name to render a textarea html element.
     *
     * @param textarea The view name to render a textarea html element.
     */
    public void setTextarea(@Nullable String textarea) {
        this.textarea = textarea;
    }

    @Override
    @Nullable
    public String getTrixEditor() {
        return trixEditor;
    }

    /**
     * The view name for a field of type trix-editor.
     *
     * @param trixEditor The view name for a field of type trix-editor.
     */
    public void setTrixEditor(@Nullable String trixEditor) {
        this.trixEditor = trixEditor;
    }
}

