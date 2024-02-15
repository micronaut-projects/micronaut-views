package io.micronaut.views.docs.turbo

import io.micronaut.core.annotation.Introspected

@Introspected
class Fruit {
    private final String name
    private final String color

    Fruit(String name, String color) {
        this.name = name
        this.color = color
    }

    String getName() {
        return name
    }

    String getColor() {
        return color
    }
}
