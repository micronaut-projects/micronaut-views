package io.micronaut.views.docs.views;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Person {
    private final String username;
    private final boolean loggedIn;

    public Person(String username, boolean loggedIn) {
        this.username = username;
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
