package io.micronaut.views.fields.tests.books

import geb.Page

class AuthorCreatePage extends Page {
    static url = "/author/create"

    static at = { $('form', action: '/author/save') }

    static content = {
        nameInput { $('#name') }
        submitInput { $('input', type: 'submit') }
    }

    void save(String name) {
        nameInput = name
        submitInput.click()
    }

}
