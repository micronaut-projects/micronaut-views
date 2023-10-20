package com.projectcheckins;

import com.projectcheckins.services.QuestionSave;
import io.micronaut.context.BeanContext;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.SerdeIntrospections;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class QuestionNewTest {

    @Inject
    BeanContext beanContext;

    @Test
    void testQuestionNewIsAnnotatedWithSerdeableDeserializable() {
        SerdeIntrospections serdeIntrospections = beanContext.getBean(SerdeIntrospections.class);
        assertDoesNotThrow(() -> serdeIntrospections.getDeserializableIntrospection(Argument.of(QuestionSave.class)));
    }

    @Test
    void testQuestionNewIsAnnotatedWithSerdeableSerializable() {
        SerdeIntrospections serdeIntrospections = beanContext.getBean(SerdeIntrospections.class);
        assertDoesNotThrow(() -> serdeIntrospections.getSerializableIntrospection(Argument.of(QuestionSave.class)));
    }
}
