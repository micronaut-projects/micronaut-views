package io.micronaut.views.html.bootstrap;

import io.micronaut.context.BeanContext;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.SerdeIntrospections;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class BreadcrumbTest {
    @Inject
    BeanContext beanContext;

    @Inject
    Validator validator;

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Breadcrumb.class));
    }
    @Test
    void isDeserializable() {
        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
        assertDoesNotThrow(() -> introspections.getDeserializableIntrospection(Argument.of(Breadcrumb.class)));
    }

    @Test
    void isSerializable() {
        SerdeIntrospections introspections = assertDoesNotThrow(() -> beanContext.getBean(SerdeIntrospections.class));
        assertDoesNotThrow(() -> introspections.getSerializableIntrospection(Argument.of(Breadcrumb.class)));
    }

    @Test
    void validBreadcrumbUpdatePassesValidation() {
        Breadcrumb breadcrumb = new Breadcrumb("Home");
        Set<ConstraintViolation<Breadcrumb>> violations = validator.validate(breadcrumb);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void invalidIdFailsValidation(String text) {
        Breadcrumb breadcrumb = new Breadcrumb(text);
        Set<ConstraintViolation<Breadcrumb>> violations = validator.validate(breadcrumb);
        ConstraintViolation<Breadcrumb> violation = violations.iterator().next();
        assertEquals("text", violation.getPropertyPath().toString());
        assertEquals("must not be blank", violation.getMessage());
    }
}
