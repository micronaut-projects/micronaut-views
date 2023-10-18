package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TextAreaFormElementTest {

    @Test
    void selectFormHasABuilderApi() {
        String name = "w3review";
        String id = "w3reviewid";
        Integer rows = 4;
        Integer cols = 50;
        String content = "At w3schools.com you will learn how to make a website. They offer free tutorials in all web development technologies.";
        TextareaFormElement formElement = TextareaFormElement.builder()
            .name(name)
            .id(id)
            .rows(rows)
            .cols(cols)
            .value(content)
            .build();
        assetTextAreaFormElement(name, id, rows, cols, content, formElement);

        BeanIntrospection<TextareaFormElement> introspection = BeanIntrospection.getIntrospection(TextareaFormElement.class);
        BeanIntrospection.Builder<TextareaFormElement> builder = introspection.builder();
        TextareaFormElement form = builder
            .with("name", name)
            .with("id", id)
            .with("rows", rows)
            .with("cols", cols)
            .with("value", content)
            .build();
        assetTextAreaFormElement(name, id, rows, cols, content, formElement);
    }

    private void assetTextAreaFormElement( String name,
                                           String id,
                                           Integer rows,
                                           Integer cols,
                                           String content,
                                           TextareaFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(name, formElement.name());
        assertEquals(id, formElement.id());
        assertEquals(rows, formElement.rows());
        assertEquals(cols, formElement.cols());
        assertEquals(content, formElement.value());
    }
}
