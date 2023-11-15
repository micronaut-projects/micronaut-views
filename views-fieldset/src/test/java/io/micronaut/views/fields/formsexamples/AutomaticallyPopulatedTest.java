package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Property(name = "spec.name", value = "AutomaticallyPopulatedTest")
@MicronautTest(startApplication = false)
class AutomaticallyPopulatedTest {

    // @DateCreated and @DateUpdated are @AutoPopulated annotations
    @Introspected
    record Book(@NotBlank String title,
                @DateCreated @Nullable LocalDate dateCreated,
                @DateUpdated @Nullable LocalDateTime lastUpdated) {
    }

    @Test
    void fieldsAnnotatedWithAutoPopulatedAreSkipped(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(Book.class);
        assertNotNull(fieldset);
        assertEquals(1, fieldset.fields().size());
    }
}
