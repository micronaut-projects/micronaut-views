package io.micronaut.views.fields.tests.signup

import io.micronaut.context.StaticMessageSource
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = "UserCreatePageSpec")
@Singleton
class PasswordMatchMessages extends StaticMessageSource {
    static final String PASSWORD_MATCH_MESSAGE = "Passwords do not match"

    private static final String MESSAGE_SUFFIX = ".message"

    PasswordMatchMessages() {
        addMessage(PasswordMatch.class.getName() + MESSAGE_SUFFIX, PASSWORD_MATCH_MESSAGE)
    }
}
