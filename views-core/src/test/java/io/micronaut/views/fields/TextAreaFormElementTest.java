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
        TextAreaFormElement formElement = TextAreaFormElement.builder()
            .name(name)
            .id(id)
            .rows(rows)
            .cols(cols)
            .content(content)
            .build();
        assetTextAreaFormElement(name, id, rows, cols, content, formElement);

        BeanIntrospection<TextAreaFormElement> introspection = BeanIntrospection.getIntrospection(TextAreaFormElement.class);
        BeanIntrospection.Builder<TextAreaFormElement> builder = introspection.builder();
        TextAreaFormElement form = builder
            .with("name", name)
            .with("id", id)
            .with("rows", rows)
            .with("cols", cols)
            .with("content", content)
            .build();
        assetTextAreaFormElement(name, id, rows, cols, content, formElement);
    }

    private void assetTextAreaFormElement( String name,
                                           String id,
                                           Integer rows,
                                           Integer cols,
                                           String content,
                                           TextAreaFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(name, formElement.getName());
        assertEquals(id, formElement.getId());
        assertEquals(rows, formElement.getRows());
        assertEquals(cols, formElement.getCols());
        assertEquals(content, formElement.getContent());
    }
}
