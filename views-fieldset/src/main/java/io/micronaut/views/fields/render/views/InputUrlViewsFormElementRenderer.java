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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.elements.InputUrlFormElement;
import jakarta.inject.Singleton;

import java.util.Map;

/**
 * {@link ViewsFormElementRenderer} implementation for {@link InputUrlFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Requires(beans = ViewsRenderer.class)
@Requires(property = FormElementRendererViewsConfigurationProperties.PREFIX + ".input-url")
@Singleton
public class InputUrlViewsFormElementRenderer extends ViewsFormElementRenderer<InputUrlFormElement> {

    /**
     *
     * @param viewsRenderer Views Renderer
     * @param formElementRendererConfiguration Form Element Renderer Configuration
     */
    public InputUrlViewsFormElementRenderer(ViewsRenderer<Map<String, FormElement>, ?> viewsRenderer,
                                            FormElementRendererViewsConfiguration formElementRendererConfiguration) {
        super(viewsRenderer, formElementRendererConfiguration.getInputUrl());
    }
}
