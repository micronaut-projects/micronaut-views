package io.micronaut.views.docs.views;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Requires(property = "spec.name", value = "ViewsControllerTest")
//tag::clazz[]
@Controller("/views")
public class ViewsController {
//end::clazz[]

    //tag::map[]
    @View("home") // <1>
    @Get
    public HttpResponse<?> index() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sdelamo"));
    }
    //end::map[]

    //tag::pojo[]
    @View("home") // <1>
    @Get("/pojo")
    public HttpResponse<Person> pojo() {
        return HttpResponse.ok(new Person("sdelamo", true));
    }
    //end::pojo[]

    //tag::modelAndView[]
    @Get("/modelAndView")
    public ModelAndView<Person> modelAndView() {
        return new ModelAndView<>("home", new Person("sdelamo", true));
    }
    //end::modelAndView[]
//tag::endclass[]
}
//end::endclass[]
