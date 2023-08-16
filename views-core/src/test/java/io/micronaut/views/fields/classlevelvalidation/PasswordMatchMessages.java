package io.micronaut.views.fields.classlevelvalidation;

import io.micronaut.context.StaticMessageSource;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Requires(property = "spec.name", value = "ClassLevelValidationTest")
@Singleton
public class PasswordMatchMessages extends StaticMessageSource {
    public static final String PASSWORD_MATCH_MESSAGE = "Passwords do not match";

    private static final String MESSAGE_SUFFIX = ".message";

    public PasswordMatchMessages() {
        addMessage(PasswordMatch.class.getName() + MESSAGE_SUFFIX, PASSWORD_MATCH_MESSAGE);
    }
}
