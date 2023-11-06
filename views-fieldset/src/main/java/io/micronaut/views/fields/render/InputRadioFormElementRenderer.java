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
import io.micronaut.views.fields.HtmlAttribute;
import io.micronaut.views.fields.InputRadioFormElement;
import io.micronaut.views.fields.Radio;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.HtmlTag.TAG_DIV;
import static io.micronaut.views.fields.HtmlTag.TAG_INPUT;
import static io.micronaut.views.fields.InputType.ATTR_TYPE_RADIO;

/**
 * Renders a {@link InputRadioFormElement} as HTML.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Secondary
@Singleton
public class InputRadioFormElementRenderer implements FormElementRenderer<InputRadioFormElement> {

    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public InputRadioFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @NonNull
    public String render(@NonNull InputRadioFormElement el,
                         @NonNull Locale locale) {
        StringBuilder html = new StringBuilder();
        for (Radio radio : el.buttons()) {
            html.append(renderOpenTag(TAG_DIV, Collections.emptyList()));
            html.append(render(TAG_INPUT, attributes(el, radio)));
            if (radio.label() != null) {
                html.append(renderLabel(radio.id(), radio.label(), messageSource, locale));
            }
            html.append(renderCloseTag(TAG_DIV));
        }
        return html.toString();
    }

    /**
     *
     * @param el input radio
     * @param radio radio option
     * @return a List of HTML attributes
     */
    protected List<HtmlAttribute> attributes(@NonNull InputRadioFormElement el,
                                             @NonNull Radio radio) {
        List<HtmlAttribute> attributes = attributes(ATTR_TYPE_RADIO);
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.required()) {
            attributes.add(new HtmlAttribute(ATTR_REQUIRED, null));
        }
        attributes.addAll(attributes(radio));
        return attributes;
    }

    /**
     *
     * @param radio Radio element
     * @return a List of HTML attributes
     */
    protected List<HtmlAttribute> attributes(@NonNull Radio radio) {
        List<HtmlAttribute> attributes = new ArrayList<>();
        attributes.add(new HtmlAttribute(ATTR_VALUE, radio.value()));
        if (radio.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, radio.id()));
        }
        if (radio.checked()) {
            attributes.add(new HtmlAttribute(ATTR_CHECKED, null));
        }
        return attributes;

    }

}
