package io.micronaut.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Requires(property = "spec.name", value = "badsyntax")
@Controller("/badsyntax")
class BadSyntaxSoyController {

    @View("fail.badsyntax")
    @Get("/")
    void badsyntax() {
    }
}
