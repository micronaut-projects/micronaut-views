package io.micronaut.views.fields;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Property(name = "spec.name", value = "FormGeneratorTest")
@MicronautTest(startApplication = false)
class FormGeneratorTest {

    @Introspected
    record BookSave(@NotBlank String title) {
    }

    @Test
    void formGenerator(FormGenerator formGenerator, BookSaveValidator validator) {
        Form form = formGenerator.generate("/book/save", "post", BookSave.class);
        Form expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title")).build(),
                InputSubmitFormElement.builder()
                    .value(Message.of("Submit", "default.input.submit.value"))
                    .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", BookSave.class, Message.of("Delete", "default.delete"));
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Delete", "default.delete"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", BookSave.class, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Foo", "default.bar"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        BookSave invalid = new BookSave(null);
        Executable e = () -> validator.validate(invalid);
        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, e);
        form = formGenerator.generate("/book/save", "post", invalid, thrown);
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .errors(Collections.singletonList(Message.of("must not be blank", "booksave.title.notblank")))
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Submit", "default.input.submit.value"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);


        form = formGenerator.generate("/book/save", "post", invalid, thrown, Message.of("Delete", "default.delete"));
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .errors(Collections.singletonList(Message.of("must not be blank", "booksave.title.notblank")))
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Delete", "default.delete"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", invalid, thrown, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .errors(Collections.singletonList(Message.of("must not be blank", "booksave.title.notblank")))
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Foo", "default.bar"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);


        BookSave valid = new BookSave(null);
        form = formGenerator.generate("/book/save", "post", valid);
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Submit", "default.input.submit.value"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);


        form = formGenerator.generate("/book/save", "post", valid, Message.of("Delete", "default.delete"));
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Delete", "default.delete"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", valid, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title")).build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Foo", "default.bar"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);
    }

    @Requires(property = "spec.name", value = "FormGeneratorTest")
    @Singleton
    static class BookSaveValidator {
        void validate(@Valid BookSave bookSave) {

        }
    }
}
