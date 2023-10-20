package com.projectcheckins.controllers;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.uri.UriBuilder;

@Controller
class HomeController {

    @Get
    HttpResponse<?> index() {
        return HttpResponse.seeOther(UriBuilder.of(QuestionController.CONTROLLER_PATH).path(QuestionController.ACTION_LIST).build());
    }
}
