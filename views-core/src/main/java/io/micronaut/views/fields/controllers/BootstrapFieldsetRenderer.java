package io.micronaut.views.fields.controllers;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpMethod;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.InputField;
import io.micronaut.views.fields.Message;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Requires(property = "micronaut.resources.html.theme", value = "bootstrap")
@Replaces(SecondaryFieldsetRenderer.class)
@Secondary
@Singleton
public class BootstrapFieldsetRenderer extends SecondaryFieldsetRenderer {
    public BootstrapFieldsetRenderer(MessageSource messageSource) {
        super(messageSource);
    }


    @Override
    public String render(Locale locale, HttpMethod method, String action, Fieldset fieldset) {
        String title = super.title(action);
        return BootstrapUtils.render(title, renderForm(locale, method, action, fieldset));
    }

    @Override
    @NonNull
    protected String renderInputSubmit(@NonNull Locale locale) {
        return "<input type=\"submit\" class=\"btn btn-primary\" value=\"" + messageSource.getMessage("default.button.create.label", "Create", locale) + "\"/>";
    }

    @Override
    protected String renderInputErrors(List<Message> errors, Locale locale) {
        String html = "";
        if (CollectionUtils.isNotEmpty(errors)) {
            html += "<div class=\"invalid-feedback\">";
            html += String.join("<br/>",
                errors
                    .stream()
                    .map(error -> messageSource.getMessage(error.getCode(), error.getDefaultMessage(), locale))
                    .toList());
            html += "</div>";
        }
        return html;
    }

    @Override
    @NonNull
    protected String renderInput(@NonNull InputField field, List<HtmlAttribute> attributes) {
        List<String> classes = new ArrayList<>();
        classes.add("form-control");
        if (field.hasErrors()) {
            classes.add("is-invalid");
        }
        return "<input class=\"" + String.join(" ", classes) + "\" "+ String.join(" ", attributes.stream().map(HtmlAttribute::toString).toList()) + "/>";
    }


    @NonNull
    protected String renderGlobalErrors(@NonNull Fieldset fieldset, @NonNull Locale locale) {
        if (!fieldset.hasErrors()) {
            return "";
        }
        String html = "<div class=\"text-danger\">";
        html += String.join("<br/>",
                fieldset.getErrors()
                .stream()
                .map(error -> messageSource.getMessage(error.getCode(), error.getDefaultMessage(), locale))
                .toList());
        html += "</div>";
        return html;

    }
}
