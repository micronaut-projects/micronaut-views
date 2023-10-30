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

    private InputTextFormElement textFormElement() {
        return textFormElementBuilder()
                .build();
    }

    private InputTextFormElement.Builder textFormElementBuilder() {
        return InputTextFormElement.builder()
                .name("title")
                .id("title")
                .required(true)
                .label(Message.of("Title", "booksave.title"));
    }

    private InputSubmitFormElement deleteSubmitFormElement() {
        return submit("Delete", "default.delete");
    }

    private InputSubmitFormElement submitFormElement() {
        return submit("Submit", "default.input.submit.value");
    }

    private InputSubmitFormElement submit(String defaultMessage, String code) {
        return InputSubmitFormElement.builder()
                .value(Message.of(defaultMessage, code))
                .build();
    }

    @Test
    void formGenerator(FormGenerator formGenerator, BookSaveValidator validator) {
        Form form = formGenerator.generate("/book/save", "post", BookSave.class);
        Form expected = new Form("/book/save", "post", new Fieldset(List.of(textFormElement(), submitFormElement()), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save",  BookSave.class);
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", BookSave.class, Message.of("Delete", "default.delete"));
        expected = new Form("/book/save", "post", new Fieldset(List.of(textFormElement(), deleteSubmitFormElement()), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", BookSave.class, Message.of("Delete", "default.delete"));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", BookSave.class, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        expected = new Form("/book/save", "post", new Fieldset(List.of(textFormElement(), submit("Foo", "default.bar")), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", BookSave.class, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        assertEquals(expected, form);

        BookSave invalid = new BookSave(null);
        Executable e = () -> validator.validate(invalid);
        ConstraintViolationException thrown = assertThrows(ConstraintViolationException.class, e);
        form = formGenerator.generate("/book/save", "post", invalid, thrown);
        expected = new Form("/book/save", "post", new Fieldset(List.of(
                textFormElementBuilder()
                .errors(Collections.singletonList(Message.of("must not be blank", "booksave.title.notblank")))
                .build(),
            submitFormElement()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", invalid, thrown);
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", invalid, thrown, Message.of("Delete", "default.delete"));
        expected = new Form("/book/save", "post", new Fieldset(List.of(
                textFormElementBuilder()
                .errors(Collections.singletonList(Message.of("must not be blank", "booksave.title.notblank")))
                .build(),
            deleteSubmitFormElement()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", invalid, thrown, Message.of("Delete", "default.delete"));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", invalid, thrown, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            textFormElementBuilder()
                .errors(Collections.singletonList(Message.of("must not be blank", "booksave.title.notblank")))
                .build(),
            InputSubmitFormElement.builder()
                .value(Message.of("Foo", "default.bar"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", invalid, thrown, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        assertEquals(expected, form);

        BookSave valid = new BookSave(null);
        form = formGenerator.generate("/book/save", "post", valid);
        expected = new Form("/book/save", "post", new Fieldset(List.of(textFormElement(), submitFormElement()), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", valid);
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", valid, Message.of("Delete", "default.delete"));
        expected = new Form("/book/save", "post", new Fieldset(List.of(
                textFormElement(),
            InputSubmitFormElement.builder()
                .value(Message.of("Delete", "default.delete"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", valid, Message.of("Delete", "default.delete"));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", "post", valid, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        expected = new Form("/book/save", "post", new Fieldset(List.of(
            textFormElement(),
            InputSubmitFormElement.builder()
                .value(Message.of("Foo", "default.bar"))
                .build()
        ), Collections.emptyList()));
        assertEquals(expected, form);

        form = formGenerator.generate("/book/save", valid, InputSubmitFormElement.builder().value(Message.of("Foo", "default.bar")).build());
        assertEquals(expected, form);
    }

    @Requires(property = "spec.name", value = "FormGeneratorTest")
    @Singleton
    static class BookSaveValidator {
        void validate(@Valid BookSave bookSave) {

        }
    }
}
