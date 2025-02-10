package io.micronaut.views.fields.messages;

import io.micronaut.context.AbstractLocalizedMessageSource;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.*;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.LocaleResolver;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.util.locale.HttpLocalizedMessageSource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "MessageTest")
@MicronautTest(startApplication = false)
class MessageTest {
    private static final Locale ES = new Locale("es");

    @Inject
    MessageSource messageSource;

    @Inject
    MessageResolver messageResolver;

    @Inject
    LocalizedMessageResolver localizedMessageResolver;

    @Test
    void messageValidation(Validator validator) {
        assertTrue(validator.validate(Message.of("Foo")).isEmpty());
        assertTrue(validator.validate(Message.of("Foo", "foo.code")).isEmpty());
        assertFalse(validator.validate(Message.of("")).isEmpty());
        String msg = null;
        assertFalse(validator.validate(Message.of(msg)).isEmpty());
    }

    @Test
    void twoArgumentConstructor() {
        assertEquals(new Message("Foo", "foo.code"), Message.of("Foo", "foo.code"));
    }

    @Test
    void threeArgumentConstructor() {
        String code = "default.null.message";
        String defaultMessage = "Property [{0}] of class [{1}] cannot be null";
        Message message = Message.of(defaultMessage, code, "pages", "Book");
        Message expected = new Message(defaultMessage, code, new Object[]{"pages", "Book"});
        assertEquals(expected, message);
    }

    @Test
    void messageSource() {
        String code = "default.null.message";
        String defaultMessage = "Property [{0}] of class [{1}] cannot be null";
        Message message = Message.of(defaultMessage, code,  "pages", "Book");
        assertEquals("La propiedad [pages] de la clase [Book] no puede ser nulo",
                messageSource.getMessage(message.code(), message.defaultMessage(), ES, message.variables()));
        assertEquals("La propiedad [pages] de la clase [Book] no puede ser nulo",
                messageResolver.getMessageOrDefault(message, ES));
        assertEquals("La propiedad [pages] de la clase [Book] no puede ser nulo",
                localizedMessageResolver.getMessageOrDefault(message));
    }

    @Requires(property = "spec.name", value = "MessageTest")
    @Factory
    static class MessageSourceFactory {
        @Singleton
        MessageSource createMessageSource() {
            return new ResourceBundleMessageSource("i18n.messages");
        }
    }

    @Requires(property = "spec.name", value = "MessageTest")
    @Primary
    @Singleton
    static class HttpLocalizedMessageSourceReplacement extends AbstractLocalizedMessageSource {
        HttpLocalizedMessageSourceReplacement(LocaleResolver<HttpRequest<?>> localeResolver, MessageSource messageSource) {
            super(localeResolver, messageSource);
        }

        @Override
        protected @NonNull Locale getLocale() {
            return ES;
        }
    }
}