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
import io.micronaut.views.fields.HtmlAttribute;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.InputType;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@link FormElementRenderer} implementation for {@link InputSubmitFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Secondary
@Singleton
public class InputSubmitFormElementRenderer implements FormElementRenderer<InputSubmitFormElement> {

    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message Source
     */
    public InputSubmitFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @NonNull
    public String render(@NonNull InputSubmitFormElement formElement, @NonNull Locale locale) {
        return render(HtmlTag.INPUT, attributes(formElement, locale));
    }

    /**
     *
     * @param el input radio
     * @param locale Request Locale
     * @return a List of HTML attributes
     */
    protected List<HtmlAttribute> attributes(@NonNull InputSubmitFormElement el, @NonNull Locale locale) {
        List<HtmlAttribute> attributes = new ArrayList<>(2);
        attributes.add(typeHtmlAttribute(InputType.SUBMIT));
        attributes.add(new HtmlAttribute(ATTR_VALUE, content(el.value(), messageSource, locale)));
        return attributes;
    }
}
