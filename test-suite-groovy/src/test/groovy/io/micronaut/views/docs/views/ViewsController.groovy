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
    HttpResponse<?> index() {
        HttpResponse.ok([loggedIn: true, username: "sdelamo"])
    }
    //end::map[]

    //tag::pojo[]
    @View("home") // <1>
    @Get("/pojo")
    HttpResponse<Person> pojo() {
        HttpResponse.ok(new Person("sdelamo", true))
    }
    //end::pojo[]

    //tag::modelAndView[]
    @Get("/modelAndView")
    ModelAndView<Person> modelAndView() {
        new ModelAndView<>("home", new Person("sdelamo", true))
    }
    //end::modelAndView[]
//tag::endclass[]
}
//end::endclass[]
