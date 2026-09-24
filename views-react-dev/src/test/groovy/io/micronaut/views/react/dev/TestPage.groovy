package io.micronaut.views.react.dev

import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.Writable
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces

/**
 * Stands in for a server-rendered React page. The filter only cares that the body is a
 * {@link Writable} and the content type is HTML, so nothing here needs GraalJS.
 */
@Requires(property = "spec.name", value = "devreload")
@Controller("/page")
class TestPage {
    @Get
    @Produces(MediaType.TEXT_HTML)
    Writable render() {
        return { writer -> writer.write("<html><body>Hello there</body></html>") } as Writable
    }

    @Get("/data")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, String> data() {
        return [hello: "there"]
    }
}
