package io.micronaut.views.fields.thymeleaf;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import io.micronaut.views.fields.FormGenerator;

import java.util.Collections;
import java.util.Map;

@Controller("/users")
class UserController {

    private final FormGenerator formGenerator;

    UserController(FormGenerator formGenerator) {
        this.formGenerator = formGenerator;
    }

    @View("/users/auth.html")
    @Get("/auth")
    @Produces(MediaType.TEXT_HTML)
    Map<String, Object> auth() {
        return Collections.singletonMap("form", formGenerator.generate("/login", Login.class));
    }
}
