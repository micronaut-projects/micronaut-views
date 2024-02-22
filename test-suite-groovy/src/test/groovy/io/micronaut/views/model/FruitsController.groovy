package io.micronaut.views.model

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.ModelAndView
import io.micronaut.views.View

@Secured(SecurityRule.IS_ANONYMOUS)
@Requires(property = "spec.name", value = "ModelAndViewSpec")
@Controller
class FruitsController {

    //tag::pojo[]
    @View("fruits")
    @Get
    Fruit index() {
        return new Fruit("apple", "red")
    }
    //end::pojo[]

    @View("fruits")
    @Get("/null")
    Object nullModel() {
        return null
    }

    @View("fruits")
    @Get("/map")
    Map<String, Object> collectionModel() {
        [fruit: new Fruit("orange", "orange")]
    }

    @Get("/processor")
    ModelAndView<Map<String, Object>> processor() {
        new ModelAndView<>("fruits-processor", [fruit: new Fruit("orange", "orange")] as Map<String, Object>)
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
