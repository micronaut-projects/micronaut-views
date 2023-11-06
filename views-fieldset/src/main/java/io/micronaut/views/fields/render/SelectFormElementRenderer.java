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
import io.micronaut.views.fields.Option;
import io.micronaut.views.fields.SelectFormElement;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static io.micronaut.views.fields.HtmlTag.TAG_SELECT;

/**
 * Renders a {@link io.micronaut.views.fields.SelectFormElement} as HTML.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Secondary
@Singleton
public class SelectFormElementRenderer implements FormElementRenderer<SelectFormElement> {

    private final MessageSource messageSource;
    private final FormElementRenderer<Option> optionFormElementRenderer;

    /**
     *
     * @param messageSource Message source.
     * @param optionFormElementRenderer Renderer for {@link Option} elements.
     */
    public SelectFormElementRenderer(MessageSource messageSource,
                                     FormElementRenderer<Option> optionFormElementRenderer) {
        this.messageSource = messageSource;
        this.optionFormElementRenderer = optionFormElementRenderer;
    }

    @Override
    @NonNull
    public String render(@NonNull SelectFormElement el,
                         @NonNull Locale locale) {
        StringBuilder html = new StringBuilder();
        if (el.label() != null) {
            html.append(renderLabel(el.id(), el.label(), messageSource, locale));
        }
        html.append(renderOpenTag(TAG_SELECT, attributes(el)));
        for (Option option : el.options()) {
            html.append(optionFormElementRenderer.render(option, locale));
        }
        html.append(renderCloseTag(TAG_SELECT));
        return html.toString();
    }

    /**
     *
     * @param el select element
     * @return a List of HTML attributes
     */
    protected List<HtmlAttribute> attributes(@NonNull SelectFormElement el) {
        List<HtmlAttribute> attributes = new ArrayList<>();
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.required()) {
            attributes.add(new HtmlAttribute(ATTR_REQUIRED, null));
        }
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        return attributes;
    }
}
