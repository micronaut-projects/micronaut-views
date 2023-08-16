package io.micronaut.views.fields.controllers;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpMethod;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.InputField;

import java.util.List;
import java.util.Locale;

public interface FieldsetRenderer {
    @NonNull
    String render(@NonNull Locale locale,
                  @NonNull HttpMethod method,
                  @NonNull String action,
                  @NonNull Fieldset fieldset);

    @NonNull
    default String render(@NonNull Locale locale,
                          @NonNull String action,
                          @NonNull Fieldset fieldset) {
        return render(locale, HttpMethod.POST, action, fieldset);
    }
}
