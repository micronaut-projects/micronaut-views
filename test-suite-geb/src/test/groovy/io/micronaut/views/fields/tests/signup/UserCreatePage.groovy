package io.micronaut.views.fields.tests.signup

import geb.Page
import geb.module.NumberInput
import geb.module.PasswordInput
import geb.module.TextInput
import io.micronaut.core.annotation.Nullable

class UserCreatePage extends Page {
    static url = "/user/create"

    static at = { $('form', action: '/user/save') }

    static content = {
        usernameInput { $('#username').module(TextInput) }
        passwordInput { $('#password').module(PasswordInput) }
        repeatPasswordInput { $('#repeatPassword').module(PasswordInput) }
        submitInput { $('input', type: 'submit') }
    }

    boolean hasGlobalErrors() {
        !$('form .text-danger').isEmpty()
    }

    void save(String name,
              String password,
              String repeatPassword) {
        usernameInput.text = name
        passwordInput.text = password
        repeatPasswordInput.text = repeatPassword
        submitInput.click()
    }
}
