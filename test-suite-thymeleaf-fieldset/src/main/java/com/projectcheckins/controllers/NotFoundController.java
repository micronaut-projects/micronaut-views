package com.projectcheckins.controllers;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import jakarta.annotation.security.PermitAll;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Controller(NotFoundController.CONTROLLER_PATH)
class NotFoundController {
    public static final String CONTROLLER_PATH = "/404";

    public static final URI CONTROLLER_URI = URI.create(CONTROLLER_PATH);

    @PermitAll
    @Produces(MediaType.TEXT_HTML)
    @Get
    @View("404.html")
    Map<String, Object> index() {
        return Collections.emptyMap();
    }
}
