package io.micronaut.views.jstachio.clazz;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Requires(property = "spec.name", value = "JStacheConfigUsingSpec")
//tag::clazz[]
@Controller("/views")
public class HomeController {

    @Produces(MediaType.TEXT_HTML)
    @Get
    @NonNull
    HomeModel index() {
        return new HomeModel("sdelamo", true);
    }
}
//end::clazz[]
