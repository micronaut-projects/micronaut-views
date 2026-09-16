package io.micronaut.views.rocker.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.rocker.RockerWritable;
import views.home;

@Requires(property = "spec.name", value = "RockerStaticTemplateTest")
@Controller("/rocker-static")
public class HomeController {

    //tag::clazz[]
    @Get
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<?> staticTemplate() {
        return HttpResponse.ok(new RockerWritable(home.template(true, "sdelamo")));
    }
    //end::clazz[]
}
