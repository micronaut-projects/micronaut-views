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
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Requires(property = "spec.name", value = "soy")
@Controller("/soy")
class SoyController {
    @View("sample.home")
    @Get
    HttpResponse index() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    @View("sample.home")
    @Get("/invalidContext")
    HttpResponse invalidContext() {
        return HttpResponse.ok(CollectionUtils.mapOf("username", "sgammon"))
    }

    @View("i.do.not.exist")
    @Get("/missing")
    HttpResponse missingTemplate() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    @Get("/home")
    HttpResponse home() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }
}
