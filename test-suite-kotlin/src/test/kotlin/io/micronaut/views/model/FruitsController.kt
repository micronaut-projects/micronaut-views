package io.micronaut.views.model

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.ModelAndView
import io.micronaut.views.View

@Secured(SecurityRule.IS_ANONYMOUS)
@Requires(property = "spec.name", value = "ModelAndViewSpec")
@Controller
class FruitsController {

    //tag::pojo[]
    @View("fruits")
    @Get
    fun index() = Fruit("apple", "red")
    //end::pojo[]

    @View("fruits")
    @Get("/null")
    fun nullModel() = null

    @View("fruits")
    @Get("/map")
    fun collectionModel() = mutableMapOf("fruit" to Fruit("orange", "orange"))

    @Get("/processor")
    fun processor() = ModelAndView("fruits-processor", mutableMapOf("fruit" to Fruit("orange", "orange")))

    @Introspected
    data class Fruit(val name: String, val color: String)
}
