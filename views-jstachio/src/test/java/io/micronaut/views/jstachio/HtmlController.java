package io.micronaut.views.jstachio;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;

@Requires(property = "spec.name", value = "HtmlControllerSpec")
@Controller("/html")
public class HtmlController {

    @Produces(MediaType.TEXT_HTML)
    @View("foo")
    @Get
    Person html() {
        return new Person("Sergio");
    }

    @Produces(MediaType.TEXT_HTML)
    @Get("/noview")
    Person htmlWithoutView() {
        return new Person("Sergio");
    }
}
