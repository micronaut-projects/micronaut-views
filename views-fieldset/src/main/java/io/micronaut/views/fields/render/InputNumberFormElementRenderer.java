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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.fields.InputNumberFormElement;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Locale;

/**
 * {@link FormElementRenderer} implementation of {@link InputNumberFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Secondary
@Singleton
public class InputNumberFormElementRenderer implements FormElementRenderer<InputNumberFormElement> {
    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public InputNumberFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String render(InputNumberFormElement el, Locale locale) {
        StringBuilder sb = new StringBuilder();
        if (el.label() != null) {
            sb.append(renderLabel(el.id(), el.label(), messageSource, locale));
        }
        sb.append(render(TAG_INPUT, attributes(el)));
        return sb.toString();
    }

    /**
     *
     * @param el input number
     * @return HTML Attributes
     */
    @NonNull
    protected List<HtmlAttribute> attributes(@NonNull InputNumberFormElement el) {
        List<HtmlAttribute> attributes = attributes(ATTR_TYPE_NUMBER);
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.value() != null) {
            attributes.add(new HtmlAttribute(ATTR_VALUE, String.valueOf(el.value())));
        }
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        if (el.min() != null) {
            attributes.add(new HtmlAttribute(ATTR_MIN, String.valueOf(el.min())));
        }
        if (el.max() != null) {
            attributes.add(new HtmlAttribute(ATTR_MAX, String.valueOf(el.max())));
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
        if (el.step() != null) {
            attributes.add(new HtmlAttribute(ATTR_STEP, el.step()));
        }
        return attributes;
    }
}
