package com.projectcheckins;

import com.projectcheckins.services.QuestionRow;
import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionRowTest {
    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(QuestionRow.class));
    }
}
