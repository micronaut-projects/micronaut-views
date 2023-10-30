package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrixEditorFormElementTest {
    @Test
    void testTag() {
        TrixEditorFormElement formElement = TrixEditorFormElement.builder().build();
        assertEquals(HtmlTag.TRIX_EDITOR, formElement.getTag());
    }

    @Test
    void trixeditorHasABuilderApi() {
        String name = "w3review";
        String id = "w3reviewid";
        String content = "At w3schools.com you will learn how to make a website. They offer free tutorials in all web development technologies.";
        TrixEditorFormElement formElement = TrixEditorFormElement.builder()
            .name(name)
            .id(id)
            .value(content)
            .build();
        assetTextAreaFormElement(name, id, content, formElement);

        BeanIntrospection<TrixEditorFormElement> introspection = BeanIntrospection.getIntrospection(TrixEditorFormElement.class);
        BeanIntrospection.Builder<TrixEditorFormElement> builder = introspection.builder();
        TrixEditorFormElement form = builder
            .with("name", name)
            .with("id", id)
            .with("value", content)
            .build();
        assetTextAreaFormElement(name, id, content, formElement);
    }

    private void assetTextAreaFormElement( String name,
                                           String id,
                                           String content,
                                           TrixEditorFormElement formElement) {
        assertNotNull(formElement);
        assertEquals(name, formElement.name());
        assertEquals(id, formElement.id());
        assertEquals(content, formElement.value());
    }
}
