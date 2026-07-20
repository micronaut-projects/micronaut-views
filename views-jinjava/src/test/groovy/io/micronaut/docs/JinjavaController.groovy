package io.micronaut.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Requires(property = "spec.name", value = "jinjava")
@Controller("/jinjava")
class JinjavaController {

    @View("home")
    @Get("/")
    HttpResponse index() {
        HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sdelamo"))
    }

    @View("home.jinja")
    @Get("/pojo")
    HttpResponse<Person> pojo() {
        HttpResponse.ok(new Person("sdelamo", true))
    }

    @View("bogus")
    @Get("/bogus")
    HttpResponse<Person> bogus() {
        HttpResponse.ok(new Person("sdelamo", true))
    }

    @View("badsyntax.jinja")
    @Get("/badsyntax")
    void badsyntax() {
    }
}
