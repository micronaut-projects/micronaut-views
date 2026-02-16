package io.micronaut.views.html.bootstrap;

public enum Color {
    DARK("dark"),
    LIGHT("light"),
    INFO("info"),
    WARNING("warning"),
    DANGER("danger"),
    SUCCESS("success"),
    SECONDARY("secondary"),
    PRIMARY("primary");

    private String value;

    Color(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
