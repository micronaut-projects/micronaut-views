package io.micronaut.views.fields.tests.location

import groovy.transform.EqualsAndHashCode
import io.micronaut.core.annotation.Nullable
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@MappedEntity
@EqualsAndHashCode
class Location {
    @Id
    @GeneratedValue
    @Nullable
    Long id

    @NotBlank
    String name

    @Max(90)
    @Min(-90)
    @Nullable
    Float latitude

    @Max(180)
    @Min(-180)
    @Nullable
    Float longitude
}
