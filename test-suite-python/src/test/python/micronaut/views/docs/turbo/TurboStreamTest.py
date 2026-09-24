from java.io import StringWriter
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views.turbo import TurboStream, TurboStreamAction
from org.junit.jupiter.api import Test


@MicronautTest(startApplication=False)
class TurboStreamTest:

    @Test
    def test_turbo_stream_fluid_api(self):
        # tag::turbofluidapi[]
        turbo_stream = (TurboStream.builder()
                        .action(TurboStreamAction.APPEND)
                        .targetDomId("dom_id")
                        .template("Content to append to container designated with the dom_id.")
                        .build())
        writable = turbo_stream.render()
        # end::turbofluidapi[]

        assert writable.isPresent()
        writer = StringWriter()
        writable.get().writeTo(writer)
        result = writer.toString()

        assert result == (
            # tag::turbofluidapiresult[]
            "<turbo-stream action=\"append\" target=\"dom_id\">"
            "<template>"
            "Content to append to container designated with the dom_id."
            "</template>"
            "</turbo-stream>"
            # end::turbofluidapiresult[]
        )
