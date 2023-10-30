package com.projectcheckins.users.controllers;

import com.projectcheckins.controllers.ResponseUtils;
import com.projectcheckins.users.services.Login;
import com.projectcheckins.users.services.Role;
import com.projectcheckins.users.services.UserSave;
import com.projectcheckins.users.services.UserService;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.FieldsetGenerator;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static com.projectcheckins.controllers.Actions.*;

@Controller(UsersController.PATH)
public class UsersController {

    public static final String RESOURCE = "users";
    public static final String VIEW_CREATE = SLASH + RESOURCE + "/create.html";

    public static final String PATH = SLASH + RESOURCE;
    private final String saveAction;
    private final URI loginUri = URI.create(SLASH + "login");
    private final String loginPath = loginUri.toString();
    private final FormGenerator formGenerator;
    private final UserService userService;

    private final FieldsetGenerator fieldsetGenerator;

    public UsersController(FormGenerator formGenerator,
                           UserService userService,
                           FieldsetGenerator fieldsetGenerator) {
        this.formGenerator = formGenerator;
        this.userService = userService;
        this.fieldsetGenerator = fieldsetGenerator;
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
    @View(VIEW_CREATE)
    @Get(PATH_CREATE)
    Map<String, Object> create() {
        return Collections.singletonMap(MODEL_KEY_FORM, formGenerator.generate(saveAction, UserSave.class));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @PermitAll
    @Produces(value = {MediaType.TEXT_HTML, TurboMediaType.TURBO_STREAM})
    @Post(PATH_SAVE)
    HttpResponse<?> save(@NonNull HttpRequest<?> request,
                         @NonNull @NotNull @Body UserSave userSave) {
        try {
            userService.save(userSave, Collections.singletonList(Role.ROLE_USER));
        } catch (ConstraintViolationException e) {
            Fieldset fieldset = fieldsetGenerator.generate(userSave, e);
            Map<String, Object> model = Collections.singletonMap(MODEL_KEY_FORM, formGenerator.generateWithFieldset(saveAction, fieldset));
            return ResponseUtils.respond(request,
                    () -> HttpResponse.unprocessableEntity().body(new ModelAndView<>(VIEW_CREATE, model)),
                    turboStream -> turboStream.replace()
                            .template("fieldset/form", model));
        }
        return HttpResponse.seeOther(loginUri);
    }
}
