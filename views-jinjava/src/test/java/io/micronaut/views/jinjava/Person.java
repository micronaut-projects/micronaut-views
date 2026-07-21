package io.micronaut.views.jinjava;

import io.micronaut.core.annotation.Introspected;

@Introspected
class Person {

    private final String username;
    private final boolean loggedIn;

    Person(String username, boolean loggedIn) {
        this.username = username;
        this.loggedIn = loggedIn;
    }

    String getUsername() {
        return username;
    }

    boolean isLoggedIn() {
        return loggedIn;
    }
}
