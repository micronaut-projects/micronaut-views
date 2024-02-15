package io.micronaut.views.docs.turbo;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public final class Fruit {
    private final String name;
    private final String color;

    public Fruit(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
