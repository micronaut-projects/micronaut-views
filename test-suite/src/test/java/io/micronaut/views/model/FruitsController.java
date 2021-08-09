/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.views.model;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

import java.util.Map;
import java.util.TreeMap;

@Secured(SecurityRule.IS_ANONYMOUS)
@Requires(property = "spec.name", value = "ModelAndViewSpec")
@Controller
public class FruitsController {

    //tag::pojo[]
    @View("fruits")
    @Get
    public Fruit index() {
        return new Fruit("apple", "red");
    }
    //end::pojo[]

    @View("fruits")
    @Get("/null")
    public Object nullModel() {
        return null;
    }

    @View("fruits")
    @Get("/map")
    public Map<String, Object> collectionModel() {
        Map<String, Object> context = new TreeMap<>();
        context.put("fruit", new Fruit("orange", "orange"));
        return context;
    }

    @Get("/processor")
    public ModelAndView<Map<String, Object>> processor() {
        Map<String, Object> context = new TreeMap<>();
        context.put("fruit", new Fruit("orange", "orange"));
        return new ModelAndView<>("fruits-processor", context);
    }

    @View("fruit")
    @Get("/pojo-processor")
    public Fruit pojoProcessor() {
        return new Fruit("orange", "orange");
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
