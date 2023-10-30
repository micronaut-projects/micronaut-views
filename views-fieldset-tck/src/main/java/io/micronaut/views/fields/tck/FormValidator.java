package io.micronaut.views.fields.tck;

import jakarta.inject.Singleton;
import jakarta.validation.Valid;

@Singleton
public class FormValidator {
    void validate(@Valid Object el) {
    }
}
