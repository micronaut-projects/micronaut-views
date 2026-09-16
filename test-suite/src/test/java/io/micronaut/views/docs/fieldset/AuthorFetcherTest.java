package io.micronaut.views.docs.fieldset;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.elements.SelectFormElement;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
class AuthorFetcherTest {

    @Inject
    FormGenerator formGenerator;

    @Test
    void theOptionsOfTheSelectAreLoadedWithTheFetcher() {
        Form form = formGenerator.generate("/books/authors/save", BookAuthorSave.class);
        assertEquals("/books/authors/save", form.action());
        InputHiddenFormElement bookId = assertInstanceOf(InputHiddenFormElement.class, form.fieldset().fields().get(0));
        assertEquals("bookId", bookId.name());
        SelectFormElement authorId = assertInstanceOf(SelectFormElement.class, form.fieldset().fields().get(1));
        assertEquals("authorId", authorId.name());
        List<Option> options = authorId.options();
        assertEquals(List.of("1", "2", "3"), options.stream().map(Option::value).toList());
        assertEquals(List.of("Kishori Sharan", "Peter Späth", "Sam Newman"), options.stream().map(o -> o.label().defaultMessage()).toList());
        assertFalse(options.stream().anyMatch(Option::selected));

        form = formGenerator.generate("/books/authors/save", new BookAuthorSave(1L, 3L));
        authorId = assertInstanceOf(SelectFormElement.class, form.fieldset().fields().get(1));
        assertEquals(List.of(false, false, true), authorId.options().stream().map(Option::selected).toList());
    }
}
