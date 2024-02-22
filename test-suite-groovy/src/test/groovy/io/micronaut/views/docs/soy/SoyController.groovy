package io.micronaut.views.docs.soy

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Requires(property = "spec.name", value = "soy")
//tag::clazz[]
@Controller("/soy")
class SoyController {

    @View("sample.home")
    @Get
    HttpResponse<?> home() {
        HttpResponse.ok([loggedIn: true, username: "sgammon"])
    }
}
//end::clazz[]
