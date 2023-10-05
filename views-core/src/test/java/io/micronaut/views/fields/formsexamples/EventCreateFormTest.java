package io.micronaut.views.fields.formsexamples;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.*;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Test
    void fieldsetGenerationForALoginForm(FieldsetGenerator fieldsetGenerator, EventCreateFormValidator validator) {
        Fieldset fieldset = fieldsetGenerator.generate(EventCreateForm.class);
        assertNotNull(fieldset);
        assertEquals(4, fieldset.getFields().size());
        InputTextFormElement nameExpectation = nameExpectation().build();
        assertTrue(assertFormElement(fieldset, nameExpectation));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof SelectFormElement));
        SelectFormElement genreExpectation = genreExpectation(null).build();
        assertTrue(assertFormElement(fieldset, genreExpectation));

        assertTrue(fieldset.getFields().stream().anyMatch(formElement -> formElement instanceof InputDataTimeLocalFormElement));
        InputDataTimeLocalFormElement eventStartExpectation = eventStartExpectation().build();
        assertTrue(assertFormElement(fieldset, eventStartExpectation));

        SelectFormElement organizerExpectation = organizerExpectation().build();
        assertTrue(assertFormElement(fieldset, organizerExpectation));

        LocalDateTime eventStart = LocalDateTime.of(2023, 8, 31, 16, 0);
        EventCreateForm valid = new EventCreateForm("Festival Gigante 2023", Genre.SPORT, eventStart, 2L);
        fieldset = fieldsetGenerator.generate(valid);
        nameExpectation = nameExpectation().value("Festival Gigante 2023").build();
        assertTrue(assertFormElement(fieldset, nameExpectation));

        genreExpectation = genreExpectation((genre, builder) -> {
            if (genre == Genre.SPORT) {
                builder.selected(true);
            }
        }).build();
        assertTrue(assertFormElement(fieldset, genreExpectation));

        EventCreateForm invalid = new EventCreateForm("", Genre.MUSIC, eventStart, 2L);
        ConstraintViolationException ex = assertThrows(ConstraintViolationException.class, () -> validator.validate(invalid));
        fieldset = fieldsetGenerator.generate(invalid, ex);
        nameExpectation = nameExpectation().value("").errors(Collections.singletonList(new SimpleMessage("eventcreateform.name.notblank", "must not be blank"))).build();
        assertTrue(assertFormElement(fieldset, nameExpectation));
    }

    private InputTextFormElement.Builder nameExpectation() {
        return InputTextFormElement.builder().required(true).id("name").name("name").label(new SimpleMessage("eventcreateform.name", "Name"));
    }

    private InputDataTimeLocalFormElement.Builder eventStartExpectation() {
        return InputDataTimeLocalFormElement.builder().required(true).id("eventStart").name("eventStart").label(new SimpleMessage("eventcreateform.eventStart", "Event Start"));
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

    @Introspected
    record EventCreateForm(@NotBlank String name,
                           @NotNull Genre genre,
                           @NotNull LocalDateTime eventStart,
                           @Select(id = "organizerId", fetcher = OrganizerOptionFetcher.class) @NotNull Long organizerId) {
    }

    @Property(name = "spec.name", value = "LoginFormTest")
    @Singleton
    static class EventCreateFormValidator {
        void validate(@Valid EventCreateForm form) {
        }
    }

    @Property(name = "spec.name", value = "LoginFormTest")
    @Singleton
    static class OrganizerOptionFetcher implements OptionFetcher {
        private final OrganizerRepository organizerRepository;

        OrganizerOptionFetcher(OrganizerRepository organizerRepository) {
            this.organizerRepository = organizerRepository;
        }

        @Override
        public <T> List<Option> generate(Class<T> type) {
            return organizerRepository.findAll()
                .stream()
                .map(organizer -> {
                    Option option = toOption(organizer, null);

                    return option;
                })
                .toList();
        }

        @Override
        public <T> List<Option> generate(T instance) {
            return organizerRepository.findAll()
                .stream()
                .map(organizer -> instance instanceof Long organizerId ? toOption(organizer, organizerId) : toOption(organizer, null))
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

}
