package io.micronaut.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View


@Requires(property = "spec.name", value = "soy")
@Controller("/soy")
class SoyController {
    @View("sample.home")
    @Get("/")
    HttpResponse index() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    @View("sample.home")
    @Get("/invalidContext")
    HttpResponse invalidContext() {
        return HttpResponse.ok(CollectionUtils.mapOf("username", "sgammon"))
    }

    @View("i.do.not.exist")
    @Get("/missing")
    HttpResponse missingTemplate() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    @Get("/home")
    HttpResponse home() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }
}
