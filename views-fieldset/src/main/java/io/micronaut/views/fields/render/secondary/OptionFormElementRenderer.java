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
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@link FormElementRenderer} implementation of {@link Option}.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Internal
@Secondary
@Singleton
public class OptionFormElementRenderer implements FormElementRenderer<Option> {

    private final MessageSource messageSource;

    /**
     *
     * @param messageSource Message source.
     */
    public OptionFormElementRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @NonNull
    public String render(@NonNull Option el,
                         @NonNull Locale locale) {
        return render(HtmlTag.OPTION, attributes(el), content(el.label(), messageSource, locale));
    }

    /**
     *
     * @param el Option
     * @return a List of HTML attributes
     */
    protected List<HtmlAttribute> attributes(@NonNull Option el) {
        List<HtmlAttribute> attributes = new ArrayList<>(3);
        attributes.add(new HtmlAttribute(ATTR_VALUE, el.value()));
        if (el.selected()) {
            attributes.add(new HtmlAttribute(ATTR_SELECTED, null));
        }
        if (el.disabled()) {
            attributes.add(new HtmlAttribute(ATTR_DISABLED, null));
        }
        return attributes;
    }
}
