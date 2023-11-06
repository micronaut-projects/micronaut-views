package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import io.micronaut.views.fields.annotations.InputHidden;
import io.micronaut.views.fields.annotations.Select;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "SaveBoxOfficeClerkFormTest")
@MicronautTest(startApplication = false)
class SaveBoxOfficeClerkFormTest {
    @Introspected
    record SaveBoxOfficeClerkForm(@InputHidden Long eventId,
                                  @Select(fetcher = BoxOfficeClerkOptionFetcher.class) @NotNull Long userId) {
    }

    @Property(name = "spec.name", value = "SaveBoxOfficeClerkFormTest")
    @Singleton
    static class SaveBoxOfficeClerkFormValidator {
        void validate(@Valid SaveBoxOfficeClerkForm form) {
        }
    }

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator) {
        SaveBoxOfficeClerkForm form = new SaveBoxOfficeClerkForm(2L,10L);
        Fieldset fieldset = fieldsetGenerator.generate(form);
        assertNotNull(fieldset);
        assertEquals(2, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), InputHiddenFormElement.class);
        assertAnyInstance(fieldset.fields(), SelectFormElement.class);

        InputHiddenFormElement eventIdExpectation = eventIdExpectation().build();
        assertTrue(assertFormElement(fieldset, eventIdExpectation));

        SelectFormElement expectation = userExpectation().build();
        assertTrue(assertFormElement(fieldset, expectation));
    }

    private InputHiddenFormElement.Builder eventIdExpectation() {
        return InputHiddenFormElement.builder()
            .name("eventId")
            .value("2");
    }

    private SelectFormElement.Builder userExpectation() {
        return SelectFormElement.builder()
            .required(true)
            .id("userId")
            .name("userId")
            .label(new SimpleMessage( "User Id", "saveboxofficeclerkform.userId"))
            .options(Arrays.asList(
                Option.builder().label(new SimpleMessage("John", null)).value("10").selected(true).build(),
                Option.builder().label(new SimpleMessage("Aegon", null)).value("12").build()
            ));
    }

    @Requires(property = "spec.name", value = "SaveBoxOfficeClerkFormTest")
    @Singleton
    static class BoxOfficeClerkOptionFetcher implements OptionFetcher<Long> {

        @Override
        public List<Option> generate(Class<Long> type) {
            return Arrays.asList(
                option(new BoxOfficeClerk(10L, "John"), null),
                option(new BoxOfficeClerk(12L, "Aegon"), null)
                );
        }

        @Override
        public List<Option> generate(Long instance) {
            return Arrays.asList(
                option(new BoxOfficeClerk(10L, "John"), instance),
                option(new BoxOfficeClerk(12L, "Aegon"), instance)
            );
        }

        @NonNull
        private Option option(@NonNull BoxOfficeClerk boxOfficeClerk,
                              @Nullable Long boxOfficeClerkId) {
            return new Option(false, boxOfficeClerk.id().equals(boxOfficeClerkId), boxOfficeClerk.id().toString(),
                new SimpleMessage(boxOfficeClerk.name(), null));
        }
    }

    @Introspected
    record BoxOfficeClerk(@NotNull Long id, @NotBlank String name) {
    }
}
