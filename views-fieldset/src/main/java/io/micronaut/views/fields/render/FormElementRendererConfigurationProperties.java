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
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link FormElementRendererConfiguration} based on a {@link ConfigurationProperties} instance.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@ConfigurationProperties(FormElementRendererConfigurationProperties.PREFIX)
public class FormElementRendererConfigurationProperties implements FormElementRendererConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".form-element-views";

    private String inputHidden;

    private String option;

    private String textarea;

    @Override
    public String getOption() {
        return option;
    }

    /**
     * The view name to render a textarea html element.
     * @param textarea The view name to render a textarea html element.
     */
    public void setTextarea(String textarea) {
        this.textarea = textarea;
    }

    @Override
    public String getTextarea() {
        return textarea;
    }

    /**
     * The view name to render an option html element.
     * @param option The view name to render an option html element
     */
    public void setOption(String option) {
        this.option = option;
    }

    @Override
    public String getInputHidden() {
        return inputHidden;
    }

    /**
     * The view name for a field of type input hidden.
     * @param inputHidden The view name for a field of type input hidden.
     */
    public void setInputHidden(String inputHidden) {
        this.inputHidden = inputHidden;
    }
}
