package io.micronaut.views.model;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.Collections;
import java.util.Map;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Requires(property = "spec.name", value = "UnmodifiableModelAndViewSpec")
@Controller
public class UnmodifiableFruitsController {

    @View("unmodifiablefruits")
    @Get("/unmodifiable")
    public Map<String, Object> unmodifiableModel() {
        return Collections.singletonMap("fruit", new Fruit("plum", "plum"));
    }

    @View("unmodifiablefruits")
    @Get("/modifiable")
    public Map<String, Object> modifiableModel() {
        return CollectionUtils.mapOf("fruit", new Fruit("plum", "plum"));
    }

    @Introspected
    public static class Fruit {
        private final String name;
        private final String color;

        public Fruit(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public String getColor() {
            return color;
        }
    }
}
