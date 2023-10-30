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
import io.micronaut.views.fields.TextareaFormElement;
import io.micronaut.views.fields.TrixEditorFormElement;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * {@link FormElementRenderer} implementation of {@link io.micronaut.views.fields.TrixEditorFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Secondary
@Singleton
public class TrixEditorFormElementRenderer implements FormElementRenderer<TrixEditorFormElement> {

    private static final String TRAG_TRIX_EDITOR = "trix-editor";
    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public TrixEditorFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String render(TrixEditorFormElement formElement, Locale locale) {
        StringBuilder sb = new StringBuilder();
        if (formElement.label() != null) {
            sb.append(renderLabel(formElement.id(), formElement.label(), messageSource, locale));
        }
        sb.append(render(TAG_INPUT, inputAttributes(formElement)));
        sb.append(render(TRAG_TRIX_EDITOR, Collections.singletonList(new HtmlAttribute(TAG_INPUT, formElement.id())), ""));
        return sb.toString();
    }

    /**
     *
     * @param el trix editor element
     * @return HTML attributes
     */
    protected List<HtmlAttribute> inputAttributes(TrixEditorFormElement el) {
        List<HtmlAttribute> attributes = new ArrayList<>();
        attributes.add(new HtmlAttribute(ATTR_TYPE, ATTR_TYPE_HIDDEN));
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        if (el.value() != null) {
            attributes.add(new HtmlAttribute(ATTR_VALUE, el.value()));
        }
        return attributes;
    }
}
