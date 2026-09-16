package io.micronaut.views.docs.fieldset

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.fields.Form
import io.micronaut.views.fields.FormGenerator
import io.micronaut.views.fields.elements.InputPasswordFormElement
import io.micronaut.views.fields.elements.InputTextFormElement
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class LoginFormSpec extends Specification {

    @Inject
    FormGenerator formGenerator

    void "the password field is rendered as a password input"() {
        when:
        Form form = formGenerator.generate("/login", Login)

        then:
        form.action() == "/login"
        form.method() == "post"
        form.fieldset().fields().size() == 3
        form.fieldset().fields()[0] instanceof InputTextFormElement
        form.fieldset().fields()[0].name() == "username"
        form.fieldset().fields()[0].required()
        form.fieldset().fields()[1] instanceof InputPasswordFormElement
        form.fieldset().fields()[1].name() == "password"
        form.fieldset().fields()[1].required()
    }
}
