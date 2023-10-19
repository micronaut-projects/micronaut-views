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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.views.fields.FormElement;
import io.micronaut.views.fields.InputStringFormElement;
import io.micronaut.views.fields.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * API to render a {@link io.micronaut.views.fields.FormElement}.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param <T> Form element
 */
@FunctionalInterface
public interface FormElementRenderer<T extends FormElement> {
    /**
     * HTML Tag input.
     */
    String TAG_INPUT = "input";

    /**
     * HTML Tag select.
     */
    String TAG_SELECT = "select";

    /**
     * HTML Tag div.
     */
    String TAG_DIV = "div";

    /**
     * HTML Tag textarea.
     */
    String TAG_TEXTAREA = "textarea";

    /**
     * HTML Tag option.
     */
    String TAG_OPTION = "option";

    /**
     * HTML Tag label.
     */
    String TAG_LABEL = "label";

    /**
     * HTML Attribute for.
     */
    String ATTR_FOR = "for";

    /**
     * HTML Attribute value.
     */
    String ATTR_VALUE = "value";

    /**
     * HTML Attribute id.
     */
    String ATTR_ID = "id";

    /**
     * HTML Attribute rows.
     */
    String ATTR_ROWS = "rows";

    /**
     * HTML Attribute cols.
     */
    String ATTR_COLS = "cols";

    /**
     * HTML Attribute placeholder.
     */
    String ATTR_PLACEHOLDER = "placeholder";

    /**
     * HTML Attribute step.
     */
    String ATTR_STEP = "step";

    /**
     * HTML Attribute required.
     */
    String ATTR_REQUIRED = "required";

    /**
     * HTML Attribute readonly.
     */
    String ATTR_READ_ONLY = "readonly";

    /**
     * HTML Attribute maxlength.
     */
    String ATTR_MAX_LENGTH = "maxlength";

    /**
     * HTML Attribute minlength.
     */
    String ATTR_MIN_LENGTH = "minlength";

    /**
     * HTML Attribute max.
     */
    String ATTR_MAX = "max";

    /**
     * HTML Attribute min.
     */
    String ATTR_MIN = "min";

    /**
     * HTML Attribute pattern.
     */
    String ATTR_PATTERN = "pattern";

    /**
     * HTML Attribute size.
     */
    String ATTR_SIZE = "size";

    /**
     * HTML Attribute name.
     */
    String ATTR_NAME = "name";

    /**
     * HTML Attribute checked.
     */
    String ATTR_CHECKED = "checked";

    /**
     * HTML Attribute type.
     */
    String ATTR_TYPE = "type";

    /**
     * HTML Input type radio.
     */
    String ATTR_TYPE_RADIO = "radio";

    /**
     * HTML Input type hidden.
     */
    String ATTR_TYPE_HIDDEN = "hidden";

    /**
     * HTML Input type number.
     */
    String ATTR_TYPE_NUMBER = "number";

    /**
     * HTML Input type date.
     */
    String ATTR_TYPE_DATE = "date";

    /**
     * HTML Input type datetime-local.
     */
    String ATTR_TYPE_DATE_TIME_LOCAL = "datetime-local";

    /**
     * HTML Input type checkbox.
     */
    String ATTR_TYPE_CHECKBOX = "checkbox";

    /**
     * HTML Input type email.
     */
    String ATTR_TYPE_EMAIL = "email";

    /**
     * HTML Input type password.
     */
    String ATTR_TYPE_PASSWORD = "password";

    /**
     * HTML Input type url.
     */
    String ATTR_TYPE_URL = "url";

    /**
     * HTML Input type text.
     */
    String ATTR_TYPE_TEXT = "text";

    /**
     * HTML Input type tel.
     */
    String ATTR_TYPE_TEL = "tel";

    /**
     * Lower than sign.
     */
    String LESS_THAN = "<";

    /**
     * Greater than sign.
     */
    String GREATER_THAN = ">";

    /**
     * Slash.
     */
    String SLASH = "/";

    /**
     * Space.
     */
    String SPACE = " ";


    /**
     * Renders a Form Element as HTML.
     * @param formElement Form Element
     * @param locale Locale
     * @return HTML
     */
    @NonNull
    String render(@NonNull T formElement,
                  @NonNull Locale locale);

    /**
     * Renders an HTML Tag.
     * @param tag HTML Tag
     * @param attributes HTML attributes
     * @return HTML
     */
    @NonNull
    default String render(@NonNull String tag,
                          @NonNull List<HtmlAttribute> attributes) {
        return LESS_THAN + tag + SPACE + attributes.stream().map(HtmlAttribute::toString).collect(Collectors.joining(SPACE)) + SLASH + GREATER_THAN;
    }

    /**
     * Renders an HTML Tag.
     * @param tag HTML Tag
     * @param attributes HTML attributes
     * @param content Content
     * @return HTML
     */
    @NonNull
    default String render(@NonNull String tag,
                          @NonNull List<HtmlAttribute> attributes,
                          @NonNull String content) {
        return renderOpenTag(tag, attributes) + content + renderCloseTag(tag);
    }

    /**
     *
     * @param id element id referenced by the label
     * @param message Message
     * @param messageSource Message Source
     * @param locale Locale
     * @return localized content
     */
    default String renderLabel(@NonNull String id,
                               @NonNull Message message,
                               @NonNull MessageSource messageSource,
                               @NonNull Locale locale) {
        return render(TAG_LABEL, id != null ? Collections.singletonList(new HtmlAttribute(ATTR_FOR, id)) : Collections.emptyList(), content(message, messageSource, locale));
    }

    /**
     *
     * @param tag an HTML Tag
     * @param attributes HTML Attributes
     * @return HTML
     */
    default String renderOpenTag(@NonNull String tag,
                                 @NonNull List<HtmlAttribute> attributes) {
        StringBuilder sb = new StringBuilder();
        sb.append(LESS_THAN);
        sb.append(tag);
        if (CollectionUtils.isNotEmpty(attributes)) {
            sb.append(SPACE);
            sb.append(attributes.stream().map(HtmlAttribute::toString).collect(Collectors.joining(SPACE)));
        }
        sb.append(GREATER_THAN);
        return sb.toString();
    }

    /**
     *
     * @param tag an HTML Tag
     * @return HTML
     */
    default String renderCloseTag(@NonNull String tag) {
        return LESS_THAN + SLASH + tag + GREATER_THAN;
    }

    /**
     *
     * @param message Message
     * @param messageSource Message Source
     * @param locale Locale
     * @return localized content
     */
    default String content(@NonNull Message message,
                           @NonNull MessageSource messageSource,
                           @NonNull Locale locale) {
        if (message.code() != null) {
            return messageSource.getMessage(message.code(), message.defaultMessage(), locale);
        }
        return message.defaultMessage();
    }

    /**
     *
     * @param type input type
     * @return HTML Attributes
     */
    @NonNull
    default List<HtmlAttribute> attributes(@NonNull String type) {
        List<HtmlAttribute> attributes = new ArrayList<>();
        attributes.add(new HtmlAttribute(ATTR_TYPE, type));
        return attributes;
    }

    /**
     *
     * @param el InputStringFormElement
     * @param type input type
     * @return HTML Attributes
     */
    @NonNull
    default List<HtmlAttribute> attributes(@NonNull InputStringFormElement el, @NonNull String type) {
        List<HtmlAttribute> attributes = attributes(type);
        attributes.add(new HtmlAttribute(ATTR_NAME, el.name()));
        if (el.value() != null) {
            attributes.add(new HtmlAttribute(ATTR_VALUE, el.value()));
        }
        if (el.id() != null) {
            attributes.add(new HtmlAttribute(ATTR_ID, el.id()));
        }
        if (el.placeholder() != null) {
            attributes.add(new HtmlAttribute(ATTR_PLACEHOLDER, el.placeholder()));
        }
        if (el.pattern() != null) {
            attributes.add(new HtmlAttribute(ATTR_PATTERN, el.pattern()));
        }
        if (el.minLength() != null) {
            attributes.add(new HtmlAttribute(ATTR_MIN_LENGTH, String.valueOf(el.minLength())));
        }
        if (el.maxLength() != null) {
            attributes.add(new HtmlAttribute(ATTR_MAX_LENGTH, String.valueOf(el.maxLength())));
        }
        if (el.size() != null) {
            attributes.add(new HtmlAttribute(ATTR_SIZE, String.valueOf(el.size())));
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
