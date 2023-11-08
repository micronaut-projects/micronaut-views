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
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.HtmlAttribute;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.HtmlTag.TAG_DIV;
import static io.micronaut.views.fields.HtmlTag.TAG_INPUT;
import static io.micronaut.views.fields.InputType.ATTR_TYPE_CHECKBOX;

/**
 * {@link FormElementRenderer} implementation for {@link InputCheckboxFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Singleton
@Secondary
public class InputCheckboxFormElementRenderer implements FormElementRenderer<InputCheckboxFormElement> {

    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public InputCheckboxFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String render(InputCheckboxFormElement formElement, Locale locale) {
        StringBuilder sb = new StringBuilder();
        for (Checkbox checkbox : formElement.checkboxes()) {
            sb.append(renderOpenTag(TAG_DIV, Collections.emptyList()));
            sb.append(render(TAG_INPUT, attributes(checkbox)));
            sb.append(renderLabel(checkbox.id(), checkbox.label(), messageSource, locale));
            sb.append(renderCloseTag(TAG_DIV));
        }
        return sb.toString();
    }

    /**
     *
     * @param el Checkbox
     * @return HTML Attributes
     */
    protected List<HtmlAttribute> attributes(Checkbox el) {
        List<HtmlAttribute> attributes = new ArrayList<>(6);
        attributes.add(typeHtmlAttribute(ATTR_TYPE_CHECKBOX));
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.value() != null) {
            attributes.add(new HtmlAttribute(ATTR_VALUE, el.value()));
        }
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        if (el.required()) {
            attributes.add(new HtmlAttribute(ATTR_REQUIRED, null));
        }
        if (el.checked()) {
            attributes.add(new HtmlAttribute(ATTR_CHECKED, null));
        }
        return attributes;
    }
}
