package io.micronaut.views.docs.fieldset;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record Author(Long id, String title) {
}
