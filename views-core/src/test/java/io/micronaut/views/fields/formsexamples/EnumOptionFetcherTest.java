package io.micronaut.views.fields.formsexamples;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.EnumOptionFetcher;
import io.micronaut.views.fields.Option;
import io.micronaut.views.fields.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class EnumOptionFetcherTest {

    @Test
    void enumToOptions(EnumOptionFetcher optionFetcher) {
        List<Option> optionList = optionFetcher.generate(Genre.class);
        assertNotNull(optionList);
        assertEquals(3, optionList.size());
        Option music = Option.builder().value("MUSIC").label(new SimpleMessage("genre.music", "Music")).build();
        Option sport = Option.builder().value("SPORT").label(new SimpleMessage("genre.sport", "Sport")).build();
        Option theater = Option.builder().value("THEATER").label(new SimpleMessage("genre.theater", "Theater")).build();
        assertTrue(optionList.stream().anyMatch(option -> option.equals(music)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(sport)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(theater)));

        optionList = optionFetcher.generate(Genre.MUSIC);
        assertNotNull(optionList);
        assertEquals(3, optionList.size());
        Option selectedMusic = Option.builder().value("MUSIC").label(new SimpleMessage("genre.music", "Music")).selected(true).build();
        assertTrue(optionList.stream().anyMatch(option -> option.equals(selectedMusic)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(sport)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(theater)));
    }
}
