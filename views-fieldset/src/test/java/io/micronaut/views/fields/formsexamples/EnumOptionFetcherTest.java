package io.micronaut.views.fields.formsexamples;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.fetchers.EnumOptionFetcher;
import io.micronaut.views.fields.elements.Option;
import io.micronaut.views.fields.messages.SimpleMessage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.micronaut.views.fields.TestUtils.assertAnyMatch;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        assertAnyMatch(optionList, music);
        assertAnyMatch(optionList, sport);
        assertAnyMatch(optionList, theater);

        optionList = optionFetcher.generate(Genre.MUSIC);
        assertNotNull(optionList);
        assertEquals(3, optionList.size());
        Option selectedMusic = Option.builder().value("MUSIC").label(new SimpleMessage("Music", "genre.music")).selected(true).build();
        assertAnyMatch(optionList, selectedMusic);
        assertAnyMatch(optionList, sport);
        assertAnyMatch(optionList, theater);
    }
}
