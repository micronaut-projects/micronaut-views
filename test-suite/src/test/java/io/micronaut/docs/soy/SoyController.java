package io.micronaut.docs.soy;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

@Requires(property = "spec.name", value = "soy")
@Controller("/soy")
public class SoyController {
    @View("sample.home")
    @Get
    public HttpResponse<?> home() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"));
    }
}
