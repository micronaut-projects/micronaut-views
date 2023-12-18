package io.micronaut.views.fields;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FormTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Form.class));
    }

    @Test
    void formEnctypeMethodGet() {
        assertThrows(IllegalArgumentException.class, () -> new Form("/foo/bar", "get", new Fieldset(Collections.emptyList(), Collections.emptyList()), "application/x-www-form-urlencoded"));
        assertDoesNotThrow(() -> new Form("/foo/bar", "post", new Fieldset(Collections.emptyList(), Collections.emptyList()), "application/x-www-form-urlencoded"));
    }
}
