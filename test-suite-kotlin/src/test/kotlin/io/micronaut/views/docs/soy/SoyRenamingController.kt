package io.micronaut.views.docs.soy

import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Requires(property = "spec.name", value = "soy")
@Controller("/soy/renaming")
class SoyRenamingController {

    @View("sample.renaming")
    @Get
    fun renaming(): Map<String, Any> = emptyMap()
}
