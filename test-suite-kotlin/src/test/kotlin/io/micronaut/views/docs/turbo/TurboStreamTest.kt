package io.micronaut.views.docs.turbo

import io.micronaut.views.turbo.TurboStream
import io.micronaut.views.turbo.TurboStreamAction
import org.junit.jupiter.api.Test
import java.io.IOException
import java.io.StringWriter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class TurboStreamTest {

    @Test
    @Throws(IOException::class)
    fun turboStreamFluidApi() {
        //tag::turbofluidapi[]
        val turboStream = TurboStream.builder()
            .action(TurboStreamAction.APPEND)
            .targetDomId("dom_id")
            .template("Content to append to container designated with the dom_id.")
            .build()
        val writable = turboStream.render()
        //end::turbofluidapi[]

        assertTrue(writable.isPresent)
        val writer = StringWriter()
        writable.get().writeTo(writer)
        val result = writer.toString()

        assertEquals(
            //tag::turbofluidapiresult[]
            "<turbo-stream action=\"append\" target=\"dom_id\">" +
                "<template>" +
                    "Content to append to container designated with the dom_id." +
                "</template>" +
            "</turbo-stream>"
            //end::turbofluidapiresult[]
        , result)
    }
}
