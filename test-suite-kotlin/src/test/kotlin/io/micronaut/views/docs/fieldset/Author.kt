package io.micronaut.views.docs.fieldset

import io.micronaut.core.annotation.Introspected

@Introspected
data class Author(val id: Long, val title: String)
