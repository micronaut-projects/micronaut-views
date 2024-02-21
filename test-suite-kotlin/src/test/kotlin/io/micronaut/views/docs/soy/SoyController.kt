package io.micronaut.views.docs.soy

import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.CollectionUtils
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
    fun home(): HttpResponse<*> {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }
}
