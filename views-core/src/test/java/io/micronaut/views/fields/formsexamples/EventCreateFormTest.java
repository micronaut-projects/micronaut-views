package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import io.micronaut.views.fields.annotations.InputRadio;
import io.micronaut.views.fields.annotations.InputUrl;
import io.micronaut.views.fields.annotations.Select;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static io.micronaut.views.fields.formsexamples.FormElementFixture.assertFormElement;
import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "EventCreateFormTest")
@MicronautTest(startApplication = false)
public class EventCreateFormTest {

    @Introspected
    record EventCreateForm(@NotBlank String name,
                           @InputRadio @NotNull Status status,

                           boolean highlighted,

                           @Positive @NotNull Integer capacity,
                           @NotNull Genre genre,
                           @NotNull LocalDateTime eventStart,
                           @Select(fetcher = OrganizerOptionFetcher.class) @NotNull Long organizerId,
                           @InputUrl @NotBlank String url,
                           String additionalInfo) {
    }

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator, EventCreateFormValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class);
        assertNotNull(fieldset);
        assertEquals(9, fieldset.getFields().size());

        InputTextFormElement nameExpectation = nameExpectation().build();
        assertTrue(assertFormElement(fieldset, nameExpectation));

        InputCheckboxFormElement highlightedExpectation = highlightedExpectation().build();
        assertTrue(assertFormElement(fieldset, highlightedExpectation));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputRadioFormElement));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputNumberFormElement));

        InputRadioFormElement statusExpectation = statusExpectation().build();
        assertTrue(assertFormElement(fieldset, statusExpectation));

        InputNumberFormElement capacityExpectation = capacityExpectation().build();
        assertTrue(assertFormElement(fieldset, capacityExpectation));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof SelectFormElement));
        SelectFormElement genreExpectation = genreExpectation(null).build();
        assertTrue(assertFormElement(fieldset, genreExpectation));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputDataTimeLocalFormElement));
        InputDataTimeLocalFormElement eventStartExpectation = eventStartExpectation().build();
        assertTrue(assertFormElement(fieldset, eventStartExpectation));

        SelectFormElement organizerExpectation = organizerExpectation().build();
        assertTrue(assertFormElement(fieldset, organizerExpectation));

        InputUrlFormElement urlExpectation = urlExpectation().build();
        assertTrue(assertFormElement(fieldset, urlExpectation));

        String url = "https://festivalgigante.com/";
        LocalDateTime eventStart = LocalDateTime.of(2023, 8, 31, 16, 0);
        EventCreateForm valid = new EventCreateForm("Festival Gigante 2023", Status.CLOSED, true, 4000, Genre.SPORT, eventStart, 2L, url, null);
        fieldset = fieldsetGenerator.generate(valid);
        nameExpectation = nameExpectation().value("Festival Gigante 2023").build();
        assertTrue(assertFormElement(fieldset, nameExpectation));

        genreExpectation = genreExpectation((genre, builder) -> {
            if (genre == Genre.SPORT) {
                builder.selected(true);
            }
        }).build();
        assertTrue(assertFormElement(fieldset, genreExpectation));

        capacityExpectation = capacityExpectation().value(4000).build();
        assertTrue(assertFormElement(fieldset, capacityExpectation));

        highlightedExpectation = highlightedExpectationChecked().build();
        assertTrue(assertFormElement(fieldset, highlightedExpectation));

        urlExpectation = urlExpectation().value(url).build();
        assertTrue(assertFormElement(fieldset, urlExpectation));

        statusExpectation = statusExpectationWithClosedStatus().build();
        assertTrue(assertFormElement(fieldset, statusExpectation));

        EventCreateForm invalid = new EventCreateForm("", Status.CLOSED, true, 4000, Genre.MUSIC,  eventStart, 2L, url, null);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        nameExpectation = nameExpectation().value("").errors(Collections.singletonList(new SimpleMessage("eventcreateform.name.notblank", "must not be blank"))).build();
        assertTrue(assertFormElement(fieldset, nameExpectation));

        highlightedExpectation = highlightedExpectationChecked().build();
        assertTrue(assertFormElement(fieldset, highlightedExpectation));

        urlExpectation = urlExpectation().value(url).build();
        assertTrue(assertFormElement(fieldset, urlExpectation));

        capacityExpectation = capacityExpectation().value(4000).build();
        assertTrue(assertFormElement(fieldset, capacityExpectation));

        statusExpectation = statusExpectationWithClosedStatus().build();
        assertTrue(assertFormElement(fieldset, statusExpectation));
    }

    private InputTextFormElement.Builder nameExpectation() {
        return InputTextFormElement.builder().required(true).id("name").name("name").label(new SimpleMessage("eventcreateform.name", "Name"));
    }

    private InputCheckboxFormElement.Builder highlightedExpectation() {
        return InputCheckboxFormElement.builder().checkboxes(Collections.singletonList(Checkbox.builder().id("highlighted").name("highlighted").value("false").label(new SimpleMessage("eventcreateform.highlighted", "Highlighted")).build()));
    }

    private InputCheckboxFormElement.Builder highlightedExpectationChecked() {
        return InputCheckboxFormElement.builder().checkboxes(Collections.singletonList(Checkbox.builder().id("highlighted").name("highlighted").value("true").checked(true).label(new SimpleMessage("eventcreateform.highlighted", "Highlighted")).build()));
    }

    private InputNumberFormElement.Builder capacityExpectation() {
        return InputNumberFormElement.builder().required(true).min(1).id("capacity").name("capacity").label(new SimpleMessage("eventcreateform.capacity", "Capacity"));
    }

    private InputRadioFormElement.Builder statusExpectation() {
        return InputRadioFormElement.builder().required(true).name("status").buttons(Arrays.asList(
            Radio.builder().value("DRAFT").id("draft").label(new SimpleMessage("status.draft", "Draft")).build(),
            Radio.builder().value("CLOSED").id("closed").label(new SimpleMessage("status.closed", "Closed")).build(),
            Radio.builder().value("OPEN").id("open").label(new SimpleMessage("status.open", "Open")).build(),
            Radio.builder().value("CANCELED").id("canceled").label(new SimpleMessage("status.canceled", "Canceled")).build()
        ));
    }

    private InputRadioFormElement.Builder statusExpectationWithClosedStatus() {
        return InputRadioFormElement.builder().required(true).name("status").buttons(Arrays.asList(
            Radio.builder().value("DRAFT").id("draft").label(new SimpleMessage("status.draft", "Draft")).build(),
            Radio.builder().value("CLOSED").id("closed").label(new SimpleMessage("status.closed", "Closed")).checked(true).build(),
            Radio.builder().value("OPEN").id("open").label(new SimpleMessage("status.open", "Open")).build(),
            Radio.builder().value("CANCELED").id("canceled").label(new SimpleMessage("status.canceled", "Canceled")).build()
        ));
    }

    private InputDataTimeLocalFormElement.Builder eventStartExpectation() {
        return InputDataTimeLocalFormElement.builder().required(true).id("eventStart").name("eventStart").label(new SimpleMessage("eventcreateform.eventStart", "Event Start"));
    }

    private InputUrlFormElement.Builder urlExpectation() {
        return InputUrlFormElement.builder().required(true).id("url").name("url").label(new SimpleMessage("eventcreateform.url", "Url"));
    }

    private SelectFormElement.Builder organizerExpectation() {
        return SelectFormElement.builder()
            .required(true)
            .id("organizerId")
            .name("organizerId")
            .label(new SimpleMessage("eventcreateform.organizerId", "Organizer Id"))
            .options(Arrays.asList(
                Option.builder().label(new SimpleMessage("organizerId.1", "Softamo SL")).value("1").build(),
                Option.builder().label(new SimpleMessage("organizerId.2", "Producciones Malvhadas")).value("2").build()
                ));
    }

    private SelectFormElement.Builder genreExpectation(BiConsumer<Genre, Option.Builder> builderConsumer) {
        List<Option> options = new ArrayList<>();

        Option.Builder musicBuilder = Option.builder().label(new SimpleMessage("genre.music", "Music")).value("MUSIC");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.MUSIC, musicBuilder);
        }
        options.add(musicBuilder.build());

        Option.Builder sportBuilder = Option.builder().label(new SimpleMessage("genre.sport", "Sport")).value("SPORT");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.SPORT, sportBuilder);
        }
        options.add(sportBuilder.build());

        Option.Builder theaterBuilder = Option.builder().label(new SimpleMessage("genre.theater", "Theater")).value("THEATER");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.THEATER, theaterBuilder);
        }
        options.add(theaterBuilder.build());

        return SelectFormElement.builder()
            .required(true)
            .id("genre")
            .name("genre")
            .label(new SimpleMessage("eventcreateform.genre", "Genre"))
            .options(options);
    }

    @Property(name = "spec.name", value = "LoginFormTest")
    @Singleton
    static class EventCreateFormValidator {
        void validate(@Valid EventCreateForm form) {
        }
    }

    @Property(name = "spec.name", value = "LoginFormTest")
    @Singleton
    static class OrganizerOptionFetcher implements OptionFetcher<Long> {
        private final OrganizerRepository organizerRepository;

        OrganizerOptionFetcher(OrganizerRepository organizerRepository) {
            this.organizerRepository = organizerRepository;
        }

        @Override
        public List<Option> generate(Class<Long> type) {
            return organizerRepository.findAll()
                .stream()
                .map(organizer -> {
                    Option option = toOption(organizer, null);

                    return option;
                })
                .toList();
        }

        @Override
        public List<Option> generate(Long organizerId) {
            return organizerRepository.findAll()
                .stream()
                .map(organizer -> toOption(organizer, organizerId))
                .toList();
        }

        @NonNull
        public static Option toOption(@NonNull Organizer product, @Nullable Long organizerId) {
            Option.Builder builder = Option.builder()
                .value(product.id().toString())
                .label(new SimpleMessage("organizerId." + product.id(), product.name()));

            if (organizerId != null) {
                if (product.id().equals(organizerId)) {
                    builder.selected(true);
                }
            }
            return builder.build();
        }
    }

    @Introspected
    record Organizer(@NotNull Long id, @NotBlank String name) {
    }

    @Property(name = "spec.name", value = "LoginFormTest")
    @Singleton
    static class OrganizerRepository {
        List<Organizer> findAll() {
            return Arrays.asList(new Organizer(1L, "Softamo SL"), new Organizer(2L, "Producciones Malvhadas"));
        }
    }

    enum Status {
        DRAFT,
        CLOSED,
        OPEN,
        CANCELED
    }

}
