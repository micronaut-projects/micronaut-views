package io.micronaut.views.docs.fieldset

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.fields.FormGenerator
import io.micronaut.views.fields.elements.InputPasswordFormElement
import io.micronaut.views.fields.elements.InputTextFormElement
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

@MicronautTest(startApplication = false)
internal class LoginFormTest {

    @Inject
    lateinit var formGenerator: FormGenerator

    @Test
    fun thePasswordFieldIsRenderedAsAPasswordInput() {
        val form = formGenerator.generate("/login", Login::class.java)
        assertEquals("/login", form.action())
        assertEquals("post", form.method())
        assertEquals(3, form.fieldset().fields().size)
        val username = assertInstanceOf(InputTextFormElement::class.java, form.fieldset().fields()[0])
        assertEquals("username", username.name())
        assertEquals(true, username.required())
        val password = assertInstanceOf(InputPasswordFormElement::class.java, form.fieldset().fields()[1])
        assertEquals("password", password.name())
        assertEquals(true, password.required())
    }
}
