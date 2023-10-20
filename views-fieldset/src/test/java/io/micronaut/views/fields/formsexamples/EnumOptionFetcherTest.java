package io.micronaut.views.fields.formsexamples;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.EnumOptionFetcher;
import io.micronaut.views.fields.Option;
import io.micronaut.views.fields.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
class EnumOptionFetcherTest {

    @Test
    void enumToOptions(EnumOptionFetcher<Genre> optionFetcher) {
        List<Option> optionList = optionFetcher.generate(Genre.class);
        assertNotNull(optionList);
        assertEquals(3, optionList.size());
        Option music = Option.builder().value("MUSIC").label(new SimpleMessage("Music", "genre.music")).build();
        Option sport = Option.builder().value("SPORT").label(new SimpleMessage("Sport", "genre.sport")).build();
        Option theater = Option.builder().value("THEATER").label(new SimpleMessage("Theater", "genre.theater")).build();
        assertTrue(optionList.stream().anyMatch(option -> option.equals(music)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(sport)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(theater)));

        optionList = optionFetcher.generate(Genre.MUSIC);
        assertNotNull(optionList);
        assertEquals(3, optionList.size());
        Option selectedMusic = Option.builder().value("MUSIC").label(new SimpleMessage("Music", "genre.music")).selected(true).build();
        assertTrue(optionList.stream().anyMatch(option -> option.equals(selectedMusic)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(sport)));
        assertTrue(optionList.stream().anyMatch(option -> option.equals(theater)));
    }
}
