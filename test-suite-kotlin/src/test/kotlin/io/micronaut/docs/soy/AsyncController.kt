package io.micronaut.docs.soy

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@Requires(property = "spec.name", value = "AsyncSpec")
@Controller("/async")
class AsyncController {

    @View("index")
    @Get("/")
    suspend fun index(): HttpResponse<Map<String, String>> = run {
        CoroutineScope(Dispatchers.IO).async {
            HttpResponse.ok(mapOf("name" to "world"))
        }.await()
    }
}
