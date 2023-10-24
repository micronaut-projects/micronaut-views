package com.projectcheckins.services;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnswerRowTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(AnswerRow.class));
    }
}