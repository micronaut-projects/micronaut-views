from typing import Annotated

import java
from jakarta.inject import Inject
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views.fields import FormGenerator
from micronaut.views.fields.elements import InputPasswordFormElement, InputTextFormElement
from org.junit.jupiter.api import Test

from .Login import Login


@MicronautTest(startApplication=False)
class LoginFormTest:
    formGenerator: Annotated[FormGenerator, Inject]

    @Test
    def test_the_password_field_is_rendered_as_a_password_input(self):
        form = self.formGenerator.generate("/login", Login)
        assert form.action() == "/login"
        assert form.method() == "post"
        fields = form.fieldset().fields()
        assert fields.size() == 3
        username = fields.get(0)
        assert java.instanceof(username, InputTextFormElement)
        assert username.name() == "username"
        assert username.required()
        password = fields.get(1)
        assert java.instanceof(password, InputPasswordFormElement)
        assert password.name() == "password"
        assert password.required()
