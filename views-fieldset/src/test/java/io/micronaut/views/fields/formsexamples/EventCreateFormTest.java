package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.annotations.InputRadio;
import io.micronaut.views.fields.annotations.InputUrl;
import io.micronaut.views.fields.annotations.Select;
import io.micronaut.views.fields.annotations.Textarea;
import io.micronaut.views.fields.elements.Checkbox;
import io.micronaut.views.fields.elements.InputCheckboxFormElement;
import io.micronaut.views.fields.elements.InputDateFormElement;
import io.micronaut.views.fields.elements.InputDateTimeLocalFormElement;
import io.micronaut.views.fields.elements.InputNumberFormElement;
import io.micronaut.views.fields.elements.InputRadioFormElement;
import io.micronaut.views.fields.elements.InputTextFormElement;
import io.micronaut.views.fields.elements.InputUrlFormElement;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.elements.Radio;
import io.micronaut.views.fields.elements.SelectFormElement;
import io.micronaut.views.fields.elements.TextareaFormElement;
import io.micronaut.views.fields.fetchers.OptionFetcher;
import io.micronaut.views.fields.messages.Message;
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

import static io.micronaut.views.fields.TestUtils.assertAnyInstance;
import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void fieldsetGenerationForAnEventCreationForm(FieldsetGenerator fieldsetGenerator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class, (propertyName, builder) -> {
            if ("eventStart".equals(propertyName)) {
                builder.with("min", DOORS_OPENING);
            }
            if ("doorsOpening".equals(propertyName)) {
                builder.with("min", SALE_CLOSING_DATE);
            }
        });
        assertNotNull(fieldset);
        assertEquals(12, fieldset.fields().size());
        assertAnyInstance(fieldset.fields(), TextareaFormElement.class);
        assertAnyInstance(fieldset.fields(), InputDateFormElement.class);

        TextareaFormElement additionalInfoExpectation = additionalInfoExpectation().build();
        assertAnyMatch(fieldset, additionalInfoExpectation);

        InputTextFormElement nameExpectation = nameExpectation().build();
        assertAnyMatch(fieldset, nameExpectation);

        InputCheckboxFormElement highlightedExpectation = highlightedExpectation().build();
        assertAnyMatch(fieldset, highlightedExpectation);

        assertAnyInstance(fieldset.fields(), InputRadioFormElement.class);

        assertAnyInstance(fieldset.fields(), InputNumberFormElement.class);

        InputRadioFormElement statusExpectation = statusExpectation().build();
        assertAnyMatch(fieldset, statusExpectation);

        InputNumberFormElement capacityExpectation = capacityExpectation().build();
        assertAnyMatch(fieldset, capacityExpectation);

        assertAnyInstance(fieldset.fields(), SelectFormElement.class);
        SelectFormElement genreExpectation = genreExpectation(null).build();
        assertAnyMatch(fieldset, genreExpectation);

        assertAnyInstance(fieldset.fields(), InputDateTimeLocalFormElement.class);
        InputDateTimeLocalFormElement eventStartExpectation = eventStartExpectation().build();
        assertAnyMatch(fieldset, eventStartExpectation);

        InputDateTimeLocalFormElement saleClosingDateExpectation = saleClosingDateExpectation().build();
        assertAnyMatch(fieldset, saleClosingDateExpectation);

        InputDateTimeLocalFormElement doorsOpeningExpectation = doorsOpeningExpectation().build();
        assertAnyMatch(fieldset, doorsOpeningExpectation);

        InputDateFormElement eventDateExpectation = eventDateExpectation().build();
        assertAnyMatch(fieldset, eventDateExpectation);

        SelectFormElement organizerExpectation = organizerExpectation().build();
        assertAnyMatch(fieldset, organizerExpectation);

        InputUrlFormElement urlExpectation = urlExpectation().build();
        assertAnyMatch(fieldset, urlExpectation);
    }

    @Test
    void fieldsetGenerationForAnEventCreationFormWithAUrl(FieldsetGenerator fieldsetGenerator) {
        String url = "https://festivalgigante.com/";
        EventCreateForm valid = new EventCreateForm("Festival Gigante 2023", Status.CLOSED, true, 4000, Genre.SPORT, EVENT_DATE, EVENT_START, DOORS_OPENING, SALE_CLOSING_DATE, 2L, url, null);
        Fieldset fieldset = fieldsetGenerator.generate(valid, (propertyName, builder) -> {
            if ("eventStart".equals(propertyName)) {
                builder.with("min", DOORS_OPENING);
            }
            if ("doorsOpening".equals(propertyName)) {
                builder.with("min", SALE_CLOSING_DATE);
            }
        });
        InputTextFormElement nameExpectation = nameExpectation().value("Festival Gigante 2023").build();
        assertAnyMatch(fieldset, nameExpectation);

        SelectFormElement genreExpectation = genreExpectation((genre, builder) -> {
            if (genre == Genre.SPORT) {
                builder.selected(true);
            }
        }).build();
        assertAnyMatch(fieldset, genreExpectation);

        InputNumberFormElement capacityExpectation = capacityExpectation().value(4000).build();
        assertAnyMatch(fieldset, capacityExpectation);

        InputCheckboxFormElement highlightedExpectation = highlightedExpectationChecked().build();
        assertAnyMatch(fieldset, highlightedExpectation);

        InputUrlFormElement urlExpectation = urlExpectation().value(url).build();
        assertAnyMatch(fieldset, urlExpectation);

        InputRadioFormElement statusExpectation = statusExpectationWithClosedStatus().build();
        assertAnyMatch(fieldset, statusExpectation);

        TextareaFormElement additionalInfoExpectation = additionalInfoExpectation().build();
        assertAnyMatch(fieldset, additionalInfoExpectation);

        InputDateFormElement eventDateExpectation = eventDateExpectation().value(EVENT_DATE).build();
        assertAnyMatch(fieldset, eventDateExpectation);
    }

    @Test
    void fieldsetGenerationForAnInvalidEventCreationForm(FieldsetGenerator fieldsetGenerator, EventCreateFormValidator validator) {
        String url = "https://festivalgigante.com/";
        EventCreateForm invalid = new EventCreateForm("", Status.CLOSED, true, 4000, Genre.MUSIC, EVENT_DATE, EVENT_START, DOORS_OPENING, SALE_CLOSING_DATE, 2L, url, "It was a dark and stormy night...");
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        Fieldset fieldset = fieldsetGenerator.generate(invalid, ex, (propertyName, builder) -> {
            if ("eventStart".equals(propertyName)) {
                builder.with("min", DOORS_OPENING);
            }
            if ("doorsOpening".equals(propertyName)) {
                builder.with("min", SALE_CLOSING_DATE);
            }
        });
        InputTextFormElement nameExpectation = nameExpectation().value("").errors(Collections.singletonList(Message.of("must not be blank", "eventcreateform.name.notblank"))).build();
        assertAnyMatch(fieldset, nameExpectation);

        InputCheckboxFormElement highlightedExpectation = highlightedExpectationChecked().build();
        assertAnyMatch(fieldset, highlightedExpectation);

        InputUrlFormElement urlExpectation = urlExpectation().value(url).build();
        assertAnyMatch(fieldset, urlExpectation);

        InputNumberFormElement capacityExpectation = capacityExpectation().value(4000).build();
        assertAnyMatch(fieldset, capacityExpectation);

        InputRadioFormElement statusExpectation = statusExpectationWithClosedStatus().build();
        assertAnyMatch(fieldset, statusExpectation);

        TextareaFormElement additionalInfoExpectation = additionalInfoExpectation().value("It was a dark and stormy night...").build();
        assertAnyMatch(fieldset, additionalInfoExpectation);

        InputDateFormElement eventDateExpectation = eventDateExpectation().value(EVENT_DATE).build();
        assertAnyMatch(fieldset, eventDateExpectation);
    }

    private TextareaFormElement.Builder additionalInfoExpectation() {
        return TextareaFormElement.builder().required(false).id("additionalInfo").name("additionalInfo").label(Message.of("Additional Info", "eventcreateform.additionalInfo"));
    }

    private InputTextFormElement.Builder nameExpectation() {
        return InputTextFormElement.builder().required(true).id("name").name("name").label(Message.of("Name", "eventcreateform.name"));
    }

    private InputCheckboxFormElement.Builder highlightedExpectation() {
        return InputCheckboxFormElement.builder()
            .label(Message.of("Highlighted", "eventcreateform.highlighted"))
            .checkboxes(Collections.singletonList(Checkbox.builder().id("highlighted").name("highlighted").value("false").label(Message.of("Highlighted", "eventcreateform.highlighted")).build()));
    }

    private InputCheckboxFormElement.Builder highlightedExpectationChecked() {
        return InputCheckboxFormElement.builder()
            .label(Message.of("Highlighted", "eventcreateform.highlighted"))
            .checkboxes(Collections.singletonList(Checkbox.builder().id("highlighted").name("highlighted").value("true").checked(true).label(Message.of("Highlighted", "eventcreateform.highlighted")).build()));
    }

    private InputNumberFormElement.Builder capacityExpectation() {
        return InputNumberFormElement.builder().required(true).min(1).id("capacity").name("capacity").label(Message.of("Capacity", "eventcreateform.capacity"));
    }

    private InputRadioFormElement.Builder statusExpectation() {
        return InputRadioFormElement.builder()
            .label(Message.of("Status", "eventcreateform.status"))
            .required(true).name("status").buttons(Arrays.asList(
            Radio.builder().value("DRAFT").id("draft").label(Message.of("Draft", "status.draft")).build(),
            Radio.builder().value("CLOSED").id("closed").label(Message.of("Closed", "status.closed")).build(),
            Radio.builder().value("OPEN").id("open").label(Message.of("Open", "status.open")).build(),
            Radio.builder().value("CANCELED").id("canceled").label(Message.of( "Canceled", "status.canceled")).build()
        ));
    }

    private InputRadioFormElement.Builder statusExpectationWithClosedStatus() {
        return InputRadioFormElement.builder()
            .label(Message.of("Status", "eventcreateform.status"))
            .required(true).name("status").buttons(Arrays.asList(
            Radio.builder().value("DRAFT").id("draft").label(Message.of("Draft", "status.draft")).build(),
            Radio.builder().value("CLOSED").id("closed").label(Message.of("Closed", "status.closed")).checked(true).build(),
            Radio.builder().value("OPEN").id("open").label(Message.of("Open", "status.open")).build(),
            Radio.builder().value("CANCELED").id("canceled").label(Message.of( "Canceled", "status.canceled")).build()
        ));
    }

    private InputDateTimeLocalFormElement.Builder eventStartExpectation() {
        return InputDateTimeLocalFormElement.builder()
            .min(DOORS_OPENING)
            .required(true)
            .id("eventStart")
            .name("eventStart")
            .label(Message.of( "Event Start", "eventcreateform.eventStart"));
    }

    private InputDateTimeLocalFormElement.Builder doorsOpeningExpectation() {
        return InputDateTimeLocalFormElement.builder()
            .min(SALE_CLOSING_DATE)
            .required(true)
            .id("doorsOpening")
            .name("doorsOpening")
            .label(Message.of( "Doors Opening", "eventcreateform.doorsOpening"));
    }

    private InputDateTimeLocalFormElement.Builder saleClosingDateExpectation() {
        return InputDateTimeLocalFormElement.builder().required(true).id("saleClosingDate").name("saleClosingDate").label(Message.of("Sale Closing Date", "eventcreateform.saleClosingDate"));
    }

    private InputDateFormElement.Builder eventDateExpectation() {
        return InputDateFormElement.builder().required(true).id("eventDate").name("eventDate").label(Message.of("Event Date", "eventcreateform.eventDate"));
    }

    private InputUrlFormElement.Builder urlExpectation() {
        return InputUrlFormElement.builder().required(true).id("url").name("url").label(Message.of("Url", "eventcreateform.url"));
    }

    private SelectFormElement.Builder organizerExpectation() {
        return SelectFormElement.builder()
            .required(true)
            .id("organizerId")
            .name("organizerId")
            .label(Message.of("Organizer Id", "eventcreateform.organizerId"))
            .options(Arrays.asList(
                Option.builder().label(Message.of("Softamo SL", null)).value("1").build(),
                Option.builder().label(Message.of("Producciones Malvhadas", null)).value("2").build()
                ));
    }

    private SelectFormElement.Builder genreExpectation(BiConsumer<Genre, Option.Builder> builderConsumer) {
        List<Option> options = new ArrayList<>();

        Option.Builder musicBuilder = Option.builder().label(Message.of("Music", "genre.music")).value("MUSIC");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.MUSIC, musicBuilder);
        }
        options.add(musicBuilder.build());

        Option.Builder sportBuilder = Option.builder().label(Message.of("Sport", "genre.sport")).value("SPORT");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.SPORT, sportBuilder);
        }
        options.add(sportBuilder.build());

        Option.Builder theaterBuilder = Option.builder().label(Message.of( "Theater", "genre.theater")).value("THEATER");
        if (builderConsumer != null) {
            builderConsumer.accept(Genre.THEATER, theaterBuilder);
        }
        options.add(theaterBuilder.build());

        return SelectFormElement.builder()
            .required(true)
            .id("genre")
            .name("genre")
            .label(Message.of("Genre", "eventcreateform.genre" ))
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
                .map(organizer -> toOption(organizer, null))
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
                .label(Message.of(organizer.name()));

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
