package io.micronaut.views.docs.turbo

import io.micronaut.core.io.Writable
import io.micronaut.views.turbo.TurboStream
import io.micronaut.views.turbo.TurboStreamAction
import spock.lang.Specification

class TurboStreamTest extends Specification {

    void "test TurboStream Fluid API"(){
        when:
//tag::turbofluidapi[]
TurboStream turboStream = TurboStream.builder()
    .action(TurboStreamAction.APPEND)
    .targetDomId("dom_id")
    .template("Content to append to container designated with the dom_id.")
    .build()
Optional<Writable> writable = turboStream.render();
//end::turbofluidapi[]
        then:
        writable.isPresent()

        when:
        StringWriter writer = new StringWriter();
        writable.get().writeTo(writer);
        String result = writer.toString();

        then:
//tag::turbofluidapiresult[]
"<turbo-stream action=\"append\" target=\"dom_id\">"+
    "<template>" +
        "Content to append to container designated with the dom_id." +
    "</template>" +
"</turbo-stream>"
//end::turbofluidapiresult[]
                == result
    }
}
