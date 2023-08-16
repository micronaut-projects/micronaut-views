package io.micronaut.views.fields.tests.books

import geb.Page
import geb.module.TextInput

class AuthorEditPage extends Page {
    @Override
    String convertToPath(Object[] args) {
        "/author/${args[0]}/edit"
    }

    static at = { $('form', action: '/author/update') }

    static content = {
        idInput { $('#id', type: 'hidden') }
        nameInput { $('#name').module(TextInput) }
        submitInput { $('input', type: 'submit') }
    }

    void update(String name) {
        nameInput = name
        submitInput.click()
    }

}
