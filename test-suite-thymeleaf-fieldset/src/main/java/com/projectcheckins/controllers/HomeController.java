package com.projectcheckins.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.uri.UriBuilder;

import java.net.URI;

import static com.projectcheckins.controllers.Actions.*;

@Controller
class HomeController {

    URI redirect;
    HomeController() {
        redirect = UriBuilder.of(QuestionController.CONTROLLER_PATH).path(ACTION_LIST).build();
    }

    @Get
    HttpResponse<?> index() {
        return HttpResponse.seeOther(redirect);
    }
}
