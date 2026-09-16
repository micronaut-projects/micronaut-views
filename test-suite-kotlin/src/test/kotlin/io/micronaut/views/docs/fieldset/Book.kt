package io.micronaut.views.docs.fieldset

import io.micronaut.core.annotation.Introspected

@Introspected
data class Book(val title: String, val pages: Int?)
