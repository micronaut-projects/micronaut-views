package io.micronaut.views.fields.formsexamples;

import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FormElement;

public class FormElementFixture {
    public static boolean assertFormElement(Fieldset fieldset, FormElement expectation) {
        return fieldset.fields().stream().anyMatch(input -> input.equals(expectation));
    }
}
