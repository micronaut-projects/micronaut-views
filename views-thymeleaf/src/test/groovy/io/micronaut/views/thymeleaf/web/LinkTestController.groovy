package io.micronaut.views.thymeleaf.web

import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Controller("/link")
@Requires(property = "spec.name", value ="LinkBuilderSpec")
class LinkTestController {
    @Get
    @View("contextRelativeUrl")
    public HttpResponse contextRelativeUrl() {
        return HttpResponse.ok()
    }

}