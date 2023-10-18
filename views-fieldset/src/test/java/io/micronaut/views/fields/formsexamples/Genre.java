package io.micronaut.views.fields.formsexamples;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;

@Introspected
public enum Genre {
    MUSIC,
    SPORT,
    THEATER;

    @Override
    public String toString() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}
