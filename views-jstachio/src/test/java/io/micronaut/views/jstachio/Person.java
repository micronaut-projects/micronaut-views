package io.micronaut.views.jstachio;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record Person(String firstName) {
}
