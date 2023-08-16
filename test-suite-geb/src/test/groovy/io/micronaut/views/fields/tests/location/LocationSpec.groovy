package io.micronaut.views.fields.tests.location

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.validation.validator.Validator
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolation
import spock.lang.Specification

@MicronautTest(startApplication = false)
class LocationSpec extends Specification {
    @Inject
    Validator validator

    void "validate latitude"() {
        given:
        Location location = new Location(id: null,
                name: "Huerto del Obispo",
                latitude: 91f,
                longitude: 91f)

        when:
        Set<ConstraintViolation<Location>> violations = validator.validate(location)

        then:
        violations

    }
}
