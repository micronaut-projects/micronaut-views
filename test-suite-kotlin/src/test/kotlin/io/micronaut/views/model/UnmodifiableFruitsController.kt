package io.micronaut.views.model

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View

@Secured(SecurityRule.IS_AUTHENTICATED)
@Requires(property = "spec.name", value = "UnmodifiableModelAndViewSpec")
@Controller
class UnmodifiableFruitsController {

    @View("unmodifiablefruits")
    @Get("/unmodifiable")
    fun unmodifiableModel() = mapOf("fruit" to Fruit("plum", "plum"))

    @View("unmodifiablefruits")
    @Get("/modifiable")
    fun modifiableModel() = mutableMapOf("fruit" to Fruit("plum", "plum"))

    @Introspected
    data class Fruit(val name: String, val color: String)
}
