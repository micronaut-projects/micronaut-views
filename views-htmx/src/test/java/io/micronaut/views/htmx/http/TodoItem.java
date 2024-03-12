package io.micronaut.views.htmx.http;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record TodoItem(Long id,
                       String title,
                       boolean completed) {
}
