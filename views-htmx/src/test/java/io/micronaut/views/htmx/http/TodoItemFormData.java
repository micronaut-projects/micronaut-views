package io.micronaut.views.htmx.http;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record TodoItemFormData(String title) {
}
