package io.micronaut.views.docs.soy;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

import java.util.Collections;
import java.util.Map;

@Requires(property = "spec.name", value = "soy")
@Controller("/soy/renaming")
public class SoyRenamingController {

    @View("sample.renaming")
    @Get
    public Map<String, Object> renaming() {
        return Collections.emptyMap();
    }
}
