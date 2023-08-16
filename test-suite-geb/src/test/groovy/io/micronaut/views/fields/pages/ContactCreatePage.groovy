package io.micronaut.views.fields.pages

import geb.Page

class ContactCreatePage extends Page {
    static url = "/contact/create"

    static at = { $('form', action: '/contact/save') }

    static content = {
        firstNameInput { $('#firstName') }
        lastNameInput { $('#lastName') }
        emailInput { $('#email') }
        submitInput { $('input', type: 'submit') }
    }

    void save(String firstName,
              String lastName,
              String email) {
        firstNameInput = firstName
        lastNameInput = lastName
        emailInput = email
        submitInput.click()
    }

}
