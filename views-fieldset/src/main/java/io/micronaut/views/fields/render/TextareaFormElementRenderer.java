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

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.views.fields.HtmlAttribute;
import io.micronaut.views.fields.TextareaFormElement;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.HtmlTag.TAG_TEXTAREA;

/**
 * {@link FormElementRenderer} implementation of {@link TextareaFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Secondary
@Singleton
public class TextareaFormElementRenderer implements FormElementRenderer<TextareaFormElement> {

    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public TextareaFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String render(TextareaFormElement formElement, Locale locale) {
        StringBuilder sb = new StringBuilder();
        if (formElement.label() != null) {
            sb.append(renderLabel(formElement.id(), formElement.label(), messageSource, locale));
        }
        sb.append(render(TAG_TEXTAREA, attributes(formElement), formElement.value()));
        return sb.toString();
    }

    /**
     *
     * @param el textarea element
     * @return HTML attributes
     */
    protected List<HtmlAttribute> attributes(TextareaFormElement el) {
        List<HtmlAttribute> attributes = new ArrayList<>();
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        if (el.cols() != null) {
            attributes.add(new HtmlAttribute(ATTR_COLS, String.valueOf(el.cols())));
        }
        if (el.rows() != null) {
            attributes.add(new HtmlAttribute(ATTR_ROWS, String.valueOf(el.rows())));
        }
        if (el.placeholder() != null) {
            attributes.add(new HtmlAttribute(ATTR_PLACEHOLDER, el.placeholder()));
        }
        if (el.required()) {
            attributes.add(new HtmlAttribute(ATTR_REQUIRED, null));
        }
        if (el.readOnly()) {
            attributes.add(new HtmlAttribute(ATTR_READ_ONLY, null));
        }
        return attributes;
    }
}
