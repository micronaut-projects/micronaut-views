/*
 * Copyright 2017-2019 original authors
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
package io.micronaut.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.async.annotation.SingleResult
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import io.micronaut.views.jstachio.JStachioModelAndView

import static io.micronaut.views.jstachio.JStachioModelAndView.JSTACHIO_VIEW;

import org.reactivestreams.Publisher


@Requires(property = "spec.name", value = "jstachio")
//tag::clazz[]
@Controller("/jstachio")
public class JStachioController {
//end::clazz[]

    //tag::pojo[]
    @View(JSTACHIO_VIEW)
    @Get("/pojo")
    public HttpResponse<Person> pojo() {
        return HttpResponse.ok(new Person("sdelamo", true));
    }
    //end::pojo[]

    @Get("/home")
    HttpResponse<Person> home() {
        return HttpResponse.ok(new Person("sdelamo", true));
    }
    
    @Get("/modelAndView")
    ModelAndView modelAndView() {
        ModelAndView modelAndView = 
            JStachioModelAndView.of(new Person("sdelamo", true));
        return modelAndView;
    }
    
    @Get("/static")
    ModelAndView staticTemplate() {
        return PersonView.of().toModelAndView(new Person("sdelamo", true));
    }

    @View("bogus")
    @Get("/bogus")
    HttpResponse<Person> bogus() {
        HttpResponse.ok( new Person("sdelamo", true))
    }

    @View(JSTACHIO_VIEW)
    @Get("/nullbody")
    HttpResponse nullBody() {
        HttpResponse.ok()
    }
}
