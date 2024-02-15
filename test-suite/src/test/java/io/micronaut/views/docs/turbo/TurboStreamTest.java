package io.micronaut.views.docs.turbo;

import io.micronaut.core.io.Writable;
import io.micronaut.views.model.FruitsController;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamAction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurboStreamTest {

    @Test
    void turboStreamFluidApi() throws IOException {
        //tag::turbofluidapi[]
        TurboStream turboStream = TurboStream.builder()
            .action(TurboStreamAction.APPEND)
            .targetDomId("dom_id")
            .template("Content to append to container designated with the dom_id.")
            .build();
        Optional<Writable> writable = turboStream.render();
        //end::turbofluidapi[]

        assertTrue(writable.isPresent());
        StringWriter writer = new StringWriter();
        writable.get().writeTo(writer);
        String result = writer.toString();

        assertEquals(
            //tag::turbofluidapiresult[]
            "<turbo-stream action=\"append\" target=\"dom_id\">" +
                "<template>" +
                    "Content to append to container designated with the dom_id." +
                "</template>" +
            "</turbo-stream>"
            //end::turbofluidapiresult[]
            , result
        );
    }
}
