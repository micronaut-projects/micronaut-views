package io.micronaut.views.fields.controllers;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.data.annotation.event.PostLoad;
import io.micronaut.http.HttpMethod;
import io.micronaut.views.fields.*;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Secondary
@Singleton
public class SecondaryFieldsetRenderer implements FieldsetRenderer {

    public static final String ATTRIBUTE_NAME_TYPE = "type";
    public static final String ATTRIBUTE_NAME_NAME = "name";
    public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_VALUE = "value";
    public static final String ATTRIBUTE_REQUIRED = "required";
    public static final String HTML_ATTRIBUTE_NAME_STEP = "step";
    public static final String HTML_ATTRIBUTE_VALUE_STEP = "any";
    protected final MessageSource messageSource;

    public SecondaryFieldsetRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    @NonNull
    public String render(@NonNull Locale locale,
                         @NonNull HttpMethod method,
                  @NonNull String action,
                  @NonNull Fieldset fieldset) {
        String title = title(action);
        return "<!DOCTYPE html><html><head><title>" + title + "</title></head><body>" + renderForm(locale, method, action, fieldset) + "</body></html>";
    }

    protected String title(String action) {
        List<String> l = new ArrayList<>(Stream.of(action.split("/")).map(StringUtils::capitalize).filter(StringUtils::isNotEmpty).toList());
        Collections.reverse(l);
        return String.join(" ", l);
    }

    @NonNull
    public String renderForm(@NonNull Locale locale,
                             @NonNull HttpMethod method,
                             @NonNull String action,
                             @NonNull Fieldset fieldset) {
        String fieldsHtml = "";
        for (FormElement field : fieldset.getFields()) {
            fieldsHtml += renderField(field, locale);
        }
        fieldsHtml += renderGlobalErrors(fieldset, locale);
        fieldsHtml += renderInputSubmit(locale);
        return "<form action=\"" + action + "\" method=\"" + method.toString() + "\">" + fieldsHtml + "</form>";
    }

    @NonNull
    protected String renderGlobalErrors(@NonNull Fieldset fieldset, @NonNull Locale locale) {
        String html = "";
        if (fieldset.hasErrors()) {
            html += renderInputErrors(fieldset.getErrors(), locale);
        }
        return html;
    }

    @NonNull
    protected String renderInputSubmit(@NonNull Locale locale) {
        return "<input type=\"submit\" value=\"" + messageSource.getMessage("default.button.create.label", "Create", locale) + "\"/>";
    }

    @NonNull
    public String renderField(@NonNull FormElement field, @NonNull Locale locale) {
//        String html = renderInputLabel(field, locale);
//        List<HtmlAttribute> attributes = attributesForField(field);
//        html += renderInput(field, attributes);
//        html += renderInputErrors(field, locale);
//        return html;
        return "";
    }

    @NonNull
    protected String renderInputLabel(@NonNull InputField field,
                                      @NonNull Locale locale) {
        if (StringUtils.isNotEmpty(field.getId()) && field.getLabel() != null) {
            Message label = field.getLabel();
            return "<label for=\"" + field.getId() + "\">" + messageSource.getMessage(label.getCode(), label.getDefaultMessage(), locale) + "</label>";
        }
        return "";
    }

    @NonNull
    protected String renderInput(@NonNull InputField field, List<HtmlAttribute> attributes) {
        return "<input "+ String.join(" ", attributes.stream().map(HtmlAttribute::toString).toList()) + "/>";
    }

    @NonNull
    protected String renderInputErrors(@NonNull InputField field,
                                       @NonNull Locale locale) {
        return renderInputErrors(field.getErrors(), locale);
    }

    @NonNull
    protected String renderInputErrors(@NonNull List<Message> errors,
                                       @NonNull Locale locale) {
        String html = "";
        if (CollectionUtils.isNotEmpty(errors)) {
            html += "<div>";
            html += String.join("<br/>",
                errors
                    .stream()
                    .map(error -> messageSource.getMessage(error.getCode(), error.getDefaultMessage(), locale))
                    .toList());
            html += "</div>";
        }
        return html;
    }


    public static List<HtmlAttribute> attributesForField(InputField field) {
        List<HtmlAttribute> attributes = new ArrayList<>();
        attributes.add(new HtmlAttribute(ATTRIBUTE_NAME_TYPE, field.getType().toString()));
        if (field.getType() == InputType.NUMBER) {
            attributes.add(new HtmlAttribute(HTML_ATTRIBUTE_NAME_STEP, HTML_ATTRIBUTE_VALUE_STEP));
        }
        attributes.add(new HtmlAttribute(ATTRIBUTE_NAME_NAME, field.getName()));
        if (StringUtils.isNotEmpty(field.getId())) {
            attributes.add(new HtmlAttribute(ATTRIBUTE_ID, field.getId()));
        }
        if (field.getValue() != null) {
            attributes.add(new HtmlAttribute(ATTRIBUTE_VALUE, field.getValue()));
        }
        if (field.isRequired()) {
            attributes.add(new HtmlAttribute(ATTRIBUTE_REQUIRED, null));
        }
        return attributes;
    }
}
