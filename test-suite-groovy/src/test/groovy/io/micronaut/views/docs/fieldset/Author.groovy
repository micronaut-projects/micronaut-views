package io.micronaut.views.docs.fieldset

import io.micronaut.core.annotation.Introspected

@Introspected
class Author {
    final Long id
    final String title

    Author(Long id, String title) {
        this.id = id
        this.title = title
    }
}
