package io.micronaut.views.model

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View

@Secured(SecurityRule.IS_AUTHENTICATED)
@Requires(property = "spec.name", value = "UnmodifiableModelAndViewSpec")
@Controller
class UnmodifiableFruitsController {

    @View("unmodifiablefruits")
    @Get("/unmodifiable")
    Map<String, Object> unmodifiableModel() {
        [fruit: new Fruit("plum", "plum")].asUnmodifiable()
    }

    @View("unmodifiablefruits")
    @Get("/modifiable")
    Map<String, Object> modifiableModel() {
        [fruit: new Fruit("plum", "plum")]
    }

    @Introspected
    static class Fruit {
        private final String name
        private final String color

        Fruit(String name, String color) {
            this.name = name
            this.color = color
        }

        String getName() {
            return name
        }

        String getColor() {
            return color
        }
    }
}
