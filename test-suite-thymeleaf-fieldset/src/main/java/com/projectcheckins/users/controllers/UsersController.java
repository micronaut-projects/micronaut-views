package com.projectcheckins.users.controllers;

import com.projectcheckins.users.services.Login;
import com.projectcheckins.users.services.UserSave;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.View;
import io.micronaut.views.fields.FormGenerator;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.Map;

import static com.projectcheckins.controllers.Actions.*;

@Controller(UsersController.PATH)
public class UsersController {

    public static final String RESOURCE = "users";

    public static final String PATH = SLASH + RESOURCE;
    private final String saveAction;

    private final String loginPath = SLASH + "login";
    private final FormGenerator formGenerator;

    public UsersController(FormGenerator formGenerator) {
        this.formGenerator = formGenerator;
        this.saveAction = UriBuilder.of(PATH).path(ACTION_SAVE).build().toString();
    }

    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    @View(PATH + "/login.html")
    @Get("/login")
    Map<String, Object> login() {
        return Collections.singletonMap(MODEL_KEY_FORM, formGenerator.generate(loginPath, Login.class));
    }

    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    @View(SLASH + RESOURCE + "/create.html")
    @Get(PATH_CREATE)
    Map<String, Object> create() {
        return Collections.singletonMap(MODEL_KEY_FORM, formGenerator.generate(saveAction, UserSave.class));
    }

    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    @Get(PATH_SAVE)
    HttpResponse<?> save(@NonNull @NotNull @Valid UserSave userSave) {
        return HttpResponse.serverError();
    }
}
