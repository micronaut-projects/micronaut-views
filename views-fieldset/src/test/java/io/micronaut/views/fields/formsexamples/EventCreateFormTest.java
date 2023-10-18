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
import io.micronaut.views.fields.annotations.Textarea;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
class EventCreateFormTest {
    private final static LocalDateTime EVENT_START = LocalDateTime.of(2023, 8, 31, 18, 0);

    private final static LocalDateTime DOORS_OPENING = LocalDateTime.of(2023, 8, 31, 16, 0);
    private final static LocalDateTime SALE_CLOSING_DATE = LocalDateTime.of(2023, 8, 30, 23, 59);
    private final static LocalDate EVENT_DATE = LocalDate.of(2023, 8, 31);

    @Introspected
    record EventCreateForm(@NotBlank String name,
                           @InputRadio @NotNull Status status,
                           boolean highlighted,
                           @Positive @NotNull Integer capacity,
                           @NotNull Genre genre,

                           @NotNull LocalDate eventDate,
                           @NotNull LocalDateTime eventStart,
                           @NotNull LocalDateTime doorsOpening,
                           @NotNull LocalDateTime saleClosingDate,
                           @Select(fetcher = OrganizerOptionFetcher.class) @NotNull Long organizerId,
                           @InputUrl @NotBlank String url,
                           @Textarea String additionalInfo) {
    }

    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator, EventCreateFormValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class, (propertyName, builder) -> {
            if ("eventStart".equals(propertyName)) {
                builder.with("min", DOORS_OPENING);
            }
            if ("doorsOpening".equals(propertyName)) {
                builder.with("min", SALE_CLOSING_DATE);
            }
        });
        assertNotNull(fieldset);
        assertEquals(12, fieldset.getFields().size());
        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof TextareaFormElement));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputDateFormElement));

        TextareaFormElement additionalInfoExpectation = additionalInfoExpectation().build();
        assertTrue(assertFormElement(fieldset, additionalInfoExpectation));

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

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputDateTimeLocalFormElement));
        InputDateTimeLocalFormElement eventStartExpectation = eventStartExpectation().build();
        assertTrue(assertFormElement(fieldset, eventStartExpectation));

        InputDateTimeLocalFormElement saleClosingDateExpectation = saleClosingDateExpectation().build();
        assertTrue(assertFormElement(fieldset, saleClosingDateExpectation));

        InputDateTimeLocalFormElement doorsOpeningExpectation = doorsOpeningExpectation().build();
        assertTrue(assertFormElement(fieldset, doorsOpeningExpectation));

        InputDateFormElement eventDateExpectation = eventDateExpectation().build();
        assertTrue(assertFormElement(fieldset, eventDateExpectation));

        SelectFormElement organizerExpectation = organizerExpectation().build();
        assertTrue(assertFormElement(fieldset, organizerExpectation));

        InputUrlFormElement urlExpectation = urlExpectation().build();
        assertTrue(assertFormElement(fieldset, urlExpectation));

        String url = "https://festivalgigante.com/";

        EventCreateForm valid = new EventCreateForm("Festival Gigante 2023", Status.CLOSED, true, 4000, Genre.SPORT, EVENT_DATE, EVENT_START, DOORS_OPENING, SALE_CLOSING_DATE, 2L, url, null);
        fieldset = fieldsetGenerator.generate(valid, (propertyName, builder) -> {
            if ("eventStart".equals(propertyName)) {
                builder.with("min", DOORS_OPENING);
            }
            if ("doorsOpening".equals(propertyName)) {
                builder.with("min", SALE_CLOSING_DATE);
            }
        });
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

        additionalInfoExpectation = additionalInfoExpectation().build();
        assertTrue(assertFormElement(fieldset, additionalInfoExpectation));

        eventDateExpectation = eventDateExpectation().value(EVENT_DATE).build();
        assertTrue(assertFormElement(fieldset, eventDateExpectation));

        EventCreateForm invalid = new EventCreateForm("", Status.CLOSED, true, 4000, Genre.MUSIC, EVENT_DATE, EVENT_START, DOORS_OPENING, SALE_CLOSING_DATE, 2L, url, "It was a dark and stormy night...");
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex, (propertyName, builder) -> {
            if ("eventStart".equals(propertyName)) {
                builder.with("min", DOORS_OPENING);
            }
            if ("doorsOpening".equals(propertyName)) {
                builder.with("min", SALE_CLOSING_DATE);
            }
        });
        nameExpectation = nameExpectation().value("").errors(Collections.singletonList(new SimpleMessage("must not be blank", "eventcreateform.name.notblank"))).build();
        assertTrue(assertFormElement(fieldset, nameExpectation));

        highlightedExpectation = highlightedExpectationChecked().build();
        assertTrue(assertFormElement(fieldset, highlightedExpectation));

        urlExpectation = urlExpectation().value(url).build();
        assertTrue(assertFormElement(fieldset, urlExpectation));

        capacityExpectation = capacityExpectation().value(4000).build();
        assertTrue(assertFormElement(fieldset, capacityExpectation));

        statusExpectation = statusExpectationWithClosedStatus().build();
        assertTrue(assertFormElement(fieldset, statusExpectation));

        additionalInfoExpectation = additionalInfoExpectation().value("It was a dark and stormy night...").build();
        assertTrue(assertFormElement(fieldset, additionalInfoExpectation));

        eventDateExpectation = eventDateExpectation().value(EVENT_DATE).build();
        assertTrue(assertFormElement(fieldset, eventDateExpectation));
    }

    private TextareaFormElement.Builder additionalInfoExpectation() {
        return TextareaFormElement.builder().required(false).id("additionalInfo").name("additionalInfo").label(new SimpleMessage("Additional Info", "eventcreateform.additionalInfo"));
    }

    private InputTextFormElement.Builder nameExpectation() {
        return InputTextFormElement.builder().required(true).id("name").name("name").label(new SimpleMessage("Name", "eventcreateform.name"));
    }

    private InputCheckboxFormElement.Builder highlightedExpectation() {
        return InputCheckboxFormElement.builder().checkboxes(Collections.singletonList(Checkbox.builder().id("highlighted").name("highlighted").value("false").label(new SimpleMessage("Highlighted", "eventcreateform.highlighted")).build()));
    }

    private InputCheckboxFormElement.Builder highlightedExpectationChecked() {
        return InputCheckboxFormElement.builder().checkboxes(Collections.singletonList(Checkbox.builder().id("highlighted").name("highlighted").value("true").checked(true).label(new SimpleMessage("Highlighted", "eventcreateform.highlighted")).build()));
    }

    private InputNumberFormElement.Builder capacityExpectation() {
        return InputNumberFormElement.builder().required(true).min(1).id("capacity").name("capacity").label(new SimpleMessage("Capacity", "eventcreateform.capacity"));
    }

    private InputRadioFormElement.Builder statusExpectation() {
        return InputRadioFormElement.builder().required(true).name("status").buttons(Arrays.asList(
            Radio.builder().value("DRAFT").id("draft").label(new SimpleMessage("Draft", "status.draft")).build(),
            Radio.builder().value("CLOSED").id("closed").label(new SimpleMessage("Closed", "status.closed")).build(),
            Radio.builder().value("OPEN").id("open").label(new SimpleMessage("Open", "status.open")).build(),
            Radio.builder().value("CANCELED").id("canceled").label(new SimpleMessage( "Canceled", "status.canceled")).build()
        ));
    }

    private InputRadioFormElement.Builder statusExpectationWithClosedStatus() {
        return InputRadioFormElement.builder().required(true).name("status").buttons(Arrays.asList(
            Radio.builder().value("DRAFT").id("draft").label(new SimpleMessage("Draft", "status.draft")).build(),
            Radio.builder().value("CLOSED").id("closed").label(new SimpleMessage("Closed", "status.closed")).checked(true).build(),
            Radio.builder().value("OPEN").id("open").label(new SimpleMessage("Open", "status.open")).build(),
            Radio.builder().value("CANCELED").id("canceled").label(new SimpleMessage( "Canceled", "status.canceled")).build()
        ));
    }

    private InputDateTimeLocalFormElement.Builder eventStartExpectation() {
        return InputDateTimeLocalFormElement.builder()
            .min(DOORS_OPENING)
            .required(true)
            .id("eventStart")
            .name("eventStart")
            .label(new SimpleMessage( "Event Start", "eventcreateform.eventStart"));
    }

    private InputDateTimeLocalFormElement.Builder doorsOpeningExpectation() {
        return InputDateTimeLocalFormElement.builder()
            .min(SALE_CLOSING_DATE)
            .required(true)
            .id("doorsOpening")
            .name("doorsOpening")
            .label(new SimpleMessage( "Doors Opening", "eventcreateform.doorsOpening"));
    }

    private InputDateTimeLocalFormElement.Builder saleClosingDateExpectation() {
        return InputDateTimeLocalFormElement.builder().required(true).id("saleClosingDate").name("saleClosingDate").label(new SimpleMessage("Sale Closing Date", "eventcreateform.saleClosingDate"));
    }

    private InputDateFormElement.Builder eventDateExpectation() {
        return InputDateFormElement.builder().required(true).id("eventDate").name("eventDate").label(new SimpleMessage("Event Date", "eventcreateform.eventDate"));
    }

    private InputUrlFormElement.Builder urlExpectation() {
        return InputUrlFormElement.builder().required(true).id("url").name("url").label(new SimpleMessage("Url", "eventcreateform.url"));
    }

    private SelectFormElement.Builder organizerExpectation() {
        return SelectFormElement.builder()
            .required(true)
            .id("organizerId")
            .name("organizerId")
            .label(new SimpleMessage("Organizer Id", "eventcreateform.organizerId"))
            .options(Arrays.asList(
                Option.builder().label(new SimpleMessage("Softamo SL", null)).value("1").build(),
                Option.builder().label(new SimpleMessage("Producciones Malvhadas", null)).value("2").build()
                ));
    }

    private SelectFormElement.Builder genreExpectation(BiConsumer<Genre, Option.Builder> builderConsumer) {
        List<Option> options = new ArrayList<>();

        Option.Builder musicBuilder = Option.builder().label(new SimpleMessage("Music", "genre.music")).value("MUSIC");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.MUSIC, musicBuilder);
        }
        options.add(musicBuilder.build());

        Option.Builder sportBuilder = Option.builder().label(new SimpleMessage("Sport", "genre.sport")).value("SPORT");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.SPORT, sportBuilder);
        }
        options.add(sportBuilder.build());

        Option.Builder theaterBuilder = Option.builder().label(new SimpleMessage( "Theater", "genre.theater")).value("THEATER");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.THEATER, theaterBuilder);
        }
        options.add(theaterBuilder.build());

        return SelectFormElement.builder()
            .required(true)
            .id("genre")
            .name("genre")
            .label(new SimpleMessage("Genre", "eventcreateform.genre" ))
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
        public static Option toOption(@NonNull Organizer organizer, @Nullable Long organizerId) {
            Option.Builder builder = Option.builder()
                .value(organizer.id().toString())
                .label(new SimpleMessage(organizer.name(), null));

            if (organizerId != null) {
                if (organizer.id().equals(organizerId)) {
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
