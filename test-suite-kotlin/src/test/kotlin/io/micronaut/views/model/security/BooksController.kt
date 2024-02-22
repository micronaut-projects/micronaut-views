package io.micronaut.views.model.security

import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View

@Requires(property = "spec.name", value = "SecurityViewModelProcessorSpec")
//tag::class[]
@Controller("/")
class BooksController {

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @View("securitydecorator")
    @Get
    fun index() = mapOf("books" to listOf("Developing Microservices"))
}
//end::class[]
