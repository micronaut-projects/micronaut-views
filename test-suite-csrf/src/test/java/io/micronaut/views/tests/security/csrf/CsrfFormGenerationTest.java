package io.micronaut.views.tests.security.csrf;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.csrf.repository.CsrfTokenRepository;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.annotations.InputPassword;
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import io.micronaut.views.fields.elements.InputPasswordFormElement;
import io.micronaut.views.fields.elements.InputSubmitFormElement;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "CsrfFormGeneration")
@MicronautTest
class CsrfFormGeneration {

    @Test
    void formIncludesAHiddenFieldForCsrfToken(FormGenerator formGenerator,
                                              @Client("/") HttpClient httpClient,
                                              MockController controller) {
        BlockingHttpClient client = httpClient.toBlocking();
        HttpResponse<?> response = assertDoesNotThrow(() -> client.exchange(HttpRequest.GET("/generate/form")));
        assertEquals(HttpStatus.ACCEPTED, response.status());
        Form form = controller.getForm();
        assertNotNull(form);
        assertEquals(2, form.fieldset().fields().stream().filter(f -> f instanceof InputPasswordFormElement).count());
        assertEquals(1, form.fieldset().fields().stream().filter(f -> f instanceof InputSubmitFormElement).count());
        assertEquals(1, form.fieldset().fields().stream().filter(f -> f instanceof InputHiddenFormElement).count());
        assertEquals(4, form.fieldset().fields().size());
        InputHiddenFormElement inputHiddenFormElement =(InputHiddenFormElement)  form.fieldset().fields().stream().filter(f -> f instanceof InputHiddenFormElement).findFirst().get();
        assertTrue(StringUtils.isNotEmpty(inputHiddenFormElement.value()));
    }

    @Serdeable
    record ChangePasswordForm(@InputPassword @NotBlank String password,
                              @InputPassword @NotBlank String repeatPassword) {
    }


    @Requires(property = "spec.name", value = "CsrfFormGeneration")
    @Singleton
    static class CsrfRepositoryMock implements CsrfTokenRepository<HttpRequest<?>> {

        @Override
        public @NonNull Optional<String> findCsrfToken(@NonNull HttpRequest<?> request) {
            return Optional.of("abcde");
        }
    }

    @Requires(property = "spec.name", value = "CsrfFormGeneration")
    @Controller("/generate/form")
    static class MockController {
        private final FormGenerator formGenerator;
        private Form form;
        MockController(FormGenerator formGenerator) {
            this.formGenerator = formGenerator;
        }

        @Secured(SecurityRule.IS_ANONYMOUS)
        @Get
        @Status(HttpStatus.ACCEPTED)
        void index() {
            this.form = formGenerator.generate("/password/change", ChangePasswordForm.class);
        }

        public Form getForm() {
            return form;
        }
    }
}
