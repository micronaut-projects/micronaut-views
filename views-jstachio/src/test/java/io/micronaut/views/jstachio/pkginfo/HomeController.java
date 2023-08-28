package io.micronaut.views.jstachio.pkginfo;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Requires(property = "spec.name", value = "HomeControllerSpec")
//tag::clazz[]
@Controller("/views")
public class HomeController {
    @Produces(MediaType.TEXT_HTML)
    @Get
    HomeModel index() {
        return new HomeModel("sdelamo", true);
    }
}
//end::clazz[]
