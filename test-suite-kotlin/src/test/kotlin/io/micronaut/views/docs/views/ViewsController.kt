package io.micronaut.views.docs.views

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.ModelAndView
import io.micronaut.views.View

@Requires(property = "spec.name", value = "ViewsControllerTest")
//tag::clazz[]
@Controller("/views")
class ViewsController {
//end::clazz[]

    //tag::map[]
    @View("home") // <1>
    @Get
    fun index(): HttpResponse<*> {
        return HttpResponse.ok(mapOf("loggedIn" to true, "username" to "sdelamo"))
    }
    //end::map[]

    //tag::pojo[]
    @View("home") // <1>
    @Get("/pojo")
    fun pojo(): HttpResponse<Person> {
        return HttpResponse.ok(Person("sdelamo", true))
    }
    //end::pojo[]

    //tag::modelAndView[]
    @Get("/modelAndView")
    fun modelAndView(): ModelAndView<Person> {
        return ModelAndView("home", Person("sdelamo", true))
    }
    //end::modelAndView[]
//tag::endclass[]
}
//end::endclass[]
