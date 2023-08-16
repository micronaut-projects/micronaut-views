package io.micronaut.views.fields.tests.location

import geb.Page
import geb.module.NumberInput
import geb.module.TextInput

class LocationEditPage extends Page {
    @Override
    String convertToPath(Object[] args) {
        "/location/${args[0]}/edit"
    }

    static at = { $('form', action: '/location/update') }

    static content = {
        idInput { $('#id', type: 'hidden') }
        nameInput { $('#name').module(TextInput) }
        latitudeInput { $('#latitude').module(NumberInput) }
        longitudeInput { $('#longitude').module(NumberInput) }
        submitInput { $('input', type: 'submit') }
    }


    void update(String name,
              Float latitude,
              Float longitude) {
        nameInput.text = name
        latitudeInput.number = latitude
        longitudeInput.number = longitude
        submitInput.click()
    }
}
