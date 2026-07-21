package io.micronaut.views.jinjava;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

import java.util.Map;

@Requires(property = "spec.name", value = "jinjava")
@Controller("/jinjava")
class JinjavaController {

    @View("home")
    @Get("/")
    HttpResponse<Map<String, Object>> index() {
        return HttpResponse.ok(Map.of("loggedIn", true, "username", "sdelamo"));
    }

    @View("home.jinja")
    @Get("/pojo")
    HttpResponse<Person> pojo() {
        return HttpResponse.ok(new Person("sdelamo", true));
    }

    @View("bogus")
    @Get("/bogus")
    HttpResponse<Person> bogus() {
        return HttpResponse.ok(new Person("sdelamo", true));
    }

    @View("badsyntax.jinja")
    @Get("/badsyntax")
    void badsyntax() {
    }
}
