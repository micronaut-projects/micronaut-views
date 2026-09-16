package io.micronaut.views.docs.views

import io.micronaut.core.annotation.Introspected

@Introspected
data class Person(val username: String, val loggedIn: Boolean)
