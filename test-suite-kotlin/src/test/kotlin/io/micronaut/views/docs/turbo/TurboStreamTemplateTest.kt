package io.micronaut.views.docs.turbo

import io.micronaut.context.ApplicationContext
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.util.StringUtils
import io.micronaut.views.turbo.TurboStream
import io.micronaut.views.turbo.TurboStreamAction
import io.micronaut.views.turbo.TurboStreamRenderer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.StringWriter
import java.util.*

class TurboStreamTemplateTest() {
    @Test
    @Throws(IOException::class)
    fun turboRendererAllowsYouToRenderTemplates() {
        val context = ApplicationContext.run(
            Collections.singletonMap<String, Any>(
                "micronaut.views.soy.enabled",
                StringUtils.FALSE
            )
        )
        val turboStreamRenderer = context.getBean(TurboStreamRenderer::class.java)
//tag::turbostreamrenderer[]
val view = "fruit"
val model = Collections.singletonMap<String, Any>("fruit", Fruit("Banana", "Yellow"))
val builder = TurboStream.builder()
    .action(TurboStreamAction.APPEND)
    .targetDomId("dom_id")
    .template(view, model)
val writable = turboStreamRenderer.render(builder, null)
//end::turbostreamrenderer[]
        Assertions.assertTrue(writable.isPresent)
        val writer = StringWriter()
        writable.get().writeTo(writer)
        val result = writer.toString()
        Assertions.assertEquals(
            "<turbo-stream action=\"append\" target=\"dom_id\">" +
                    "<template>" +
                    "<h1>fruit: Banana</h1>\n" +
                    "<h2>color: Yellow</h2>" +
                    "</template>" +
                    "</turbo-stream>", result
        )
        context.close()
    }

    @Introspected
    class Fruit(val name: String, val color: String)
}