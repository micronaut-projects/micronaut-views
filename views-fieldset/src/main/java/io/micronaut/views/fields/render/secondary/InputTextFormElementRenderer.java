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
package io.micronaut.views.fields.render.secondary;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.InputType;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Singleton;

import java.util.Locale;

/**
 * {@link FormElementRenderer} implementation of {@link InputTextFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Secondary
@Singleton
public class InputTextFormElementRenderer implements FormElementRenderer<InputTextFormElement> {

    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public InputTextFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @NonNull
    public String render(@NonNull InputTextFormElement el,
                         @NonNull Locale locale) {
        StringBuilder sb = new StringBuilder();
        if (el.label() != null) {
            sb.append(renderLabel(el.id(), el.label(), messageSource, locale));
        }
        sb.append(render(HtmlTag.INPUT, attributes(el, InputType.TEXT)));
        return sb.toString();
    }
}
