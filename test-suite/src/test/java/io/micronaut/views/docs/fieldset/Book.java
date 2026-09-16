package io.micronaut.views.docs.fieldset;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Book {
    private final String title;
    private final Integer pages;

    public Book(String title, Integer pages) {
        this.title = title;
        this.pages = pages;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPages() {
        return pages;
    }
}
