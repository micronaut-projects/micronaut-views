package io.micronaut.views.fields.tests.location

import geb.Page
import geb.module.NumberInput
import geb.module.TextInput
import io.micronaut.core.annotation.Nullable

class LocationCreatePage extends Page {
    static url = "/location/create"

    static at = { $('form', action: '/location/save') }

    static content = {
        nameInput { $('#name').module(TextInput) }
        latitudeInput { $('#latitude').module(NumberInput) }
        longitudeInput { $('#longitude').module(NumberInput) }
        submitInput { $('input', type: 'submit') }
    }

    void save(String name,
              @Nullable Float latitude,
              @Nullable Float longitude) {
        nameInput.text = name
        if (latitude) {
            latitudeInput.number = latitude
        }
        if (longitude) {
            longitudeInput.number = longitude
        }
        submitInput.click()
    }

}
