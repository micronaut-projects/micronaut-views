package io.micronaut.views.docs.views

import io.micronaut.core.annotation.Introspected

@Introspected
class Person {
    final String username
    final boolean loggedIn

    Person(String username, boolean loggedIn) {
        this.username = username
        this.loggedIn = loggedIn
    }
}
