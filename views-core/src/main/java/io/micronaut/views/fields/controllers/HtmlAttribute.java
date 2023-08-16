package io.micronaut.views.fields.controllers;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

@Introspected
public record HtmlAttribute(@NonNull String key, @Nullable String value) {
    @Override
    public String toString() {
        if (value != null) {
            return key + "=\"" + value + "\"";
        }
        return key;
    }
}
