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
import io.micronaut.views.fields.InputDateFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.HtmlTag.TAG_INPUT;
import static io.micronaut.views.fields.InputType.ATTR_TYPE_DATE;

/**
 * {@link FormElementRenderer} implementation of {@link InputDateFormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Secondary
@Singleton
public class InputDateFormElementRenderer implements FormElementRenderer<InputDateFormElement> {
    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public InputDateFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String render(InputDateFormElement el, Locale locale) {
        StringBuilder sb = new StringBuilder();
        if (el.label() != null) {
            sb.append(renderLabel(el.id(), el.label(), messageSource, locale));
        }
        sb.append(render(TAG_INPUT, attributes(el)));
        return sb.toString();
    }

    /**
     *
     * @param el input date
     * @return HTML attributes
     */
    @NonNull
    protected List<HtmlAttribute> attributes(@NonNull InputDateFormElement el) {
        List<HtmlAttribute> attributes = attributes(ATTR_TYPE_DATE);
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.value() != null) {
            attributes.add(new HtmlAttribute(ATTR_VALUE, el.value().toString()));
        }
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        if (el.min() != null) {
            attributes.add(new HtmlAttribute(ATTR_MIN, el.min().toString()));
        }
        if (el.max() != null) {
            attributes.add(new HtmlAttribute(ATTR_MAX, el.max().toString()));
        }
        if (el.required()) {
            attributes.add(new HtmlAttribute(ATTR_REQUIRED, null));
        }
        return attributes;
    }
}
