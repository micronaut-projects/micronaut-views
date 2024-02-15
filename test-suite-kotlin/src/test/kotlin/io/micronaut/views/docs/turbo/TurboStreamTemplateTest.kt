package io.micronaut.views.docs.turbo

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.StringUtils
import io.micronaut.views.turbo.TurboStream
import io.micronaut.views.turbo.TurboStreamAction
import io.micronaut.views.turbo.TurboStreamRenderer
import java.io.IOException
import java.io.StringWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TurboStreamTemplateTest() {

    @Test
    @Throws(IOException::class)
    fun turboRendererAllowsYouToRenderTemplates() {
        val context = ApplicationContext.run(mapOf("micronaut.views.soy.enabled" to StringUtils.FALSE))

        val turboStreamRenderer = context.getBean(TurboStreamRenderer::class.java)
        //tag::turbostreamrenderer[]
        val view = "fruit"
        val model = mapOf("fruit" to Fruit("Banana", "Yellow"))
        val builder = TurboStream.builder()
            .action(TurboStreamAction.APPEND)
            .targetDomId("dom_id")
            .template(view, model)
        val writable = turboStreamRenderer.render(builder, null)
        //end::turbostreamrenderer[]

        assertTrue(writable.isPresent)
        val writer = StringWriter()
        writable.get().writeTo(writer)
        val result = writer.toString()
        assertEquals(
            "<turbo-stream action=\"append\" target=\"dom_id\">" +
                    "<template>" +
                    "<h1>fruit: Banana</h1>\n" +
                    "<h2>color: Yellow</h2>\n" +
                    "</template>" +
                    "</turbo-stream>", result
        )
        context.close()
    }
}
