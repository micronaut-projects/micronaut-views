package io.micronaut.views.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Book {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
