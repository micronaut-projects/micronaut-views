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
import io.micronaut.views.fields.HtmlAttribute;
import io.micronaut.core.annotation.Internal;
import io.micronaut.views.fields.HtmlTag;
import io.micronaut.views.fields.elements.TrixEditorFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.InputType.ATTR_TYPE_HIDDEN;

/**
 * {@link FormElementRenderer} implementation of {@link TrixEditorFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Secondary
@Singleton
public class TrixEditorFormElementRenderer implements FormElementRenderer<TrixEditorFormElement> {

    private static final String TAG_TRIX_EDITOR = "trix-editor";
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
        sb.append(render(HtmlTag.INPUT, inputAttributes(formElement)));
        sb.append(render(HtmlTag.TRIX_EDITOR, Collections.singletonList(new HtmlAttribute(HtmlTag.INPUT.toString(), formElement.id())), ""));
        return sb.toString();
    }

    /**
     *
     * @param el trix editor element
     * @return HTML attributes
     */
    protected List<HtmlAttribute> inputAttributes(TrixEditorFormElement el) {
        List<HtmlAttribute> attributes = new ArrayList<>(4);
        attributes.add(typeHtmlAttribute(ATTR_TYPE_HIDDEN));
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
