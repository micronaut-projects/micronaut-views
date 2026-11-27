package io.micronaut.views.react;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

import java.time.Instant;
import java.util.List;

/**
 * Props whose JSON is not what their properties are: an enum, an instant, a property with another name in JSON and
 * one that JSON leaves out.
 *
 * @param title A title
 * @param genre An enum
 * @param added An instant
 * @param pages A property that JSON names {@code pageCount}
 * @param secret A property that JSON leaves out
 * @param tags A list
 */
@Serdeable
public record CardProps(String title,
                        Genre genre,
                        Instant added,
                        @JsonProperty("pageCount") int pages,
                        @JsonIgnore String secret,
                        List<String> tags) {

    /**
     * A kind of book.
     */
    public enum Genre {
        NOVEL,
        ESSAY
    }
}
