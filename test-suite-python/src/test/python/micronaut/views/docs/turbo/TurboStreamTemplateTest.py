from typing import Annotated

from jakarta.inject import Inject
from java.io import StringWriter
from micronaut.context.annotation import Property
from micronaut.test.extensions.junit5.annotation import MicronautTest
from micronaut.views.turbo import TurboStream, TurboStreamAction, TurboStreamRenderer
from org.junit.jupiter.api import Test

from .Fruit import Fruit


@Property(name="micronaut.views.soy.enabled", value="false")
@MicronautTest(startApplication=False)
class TurboStreamTemplateTest:
    turboStreamRenderer: Annotated[TurboStreamRenderer, Inject]

    @Test
    def test_turbo_renderer_allows_you_to_render_templates(self):
        turbo_stream_renderer = self.turboStreamRenderer
        # tag::turbostreamrenderer[]
        view = "fruit"
        model = {"fruit": Fruit("Banana", "Yellow")}
        builder = (TurboStream.builder()
                   .action(TurboStreamAction.APPEND)
                   .targetDomId("dom_id")
                   .template(view, model))
        writable = turbo_stream_renderer.render(builder, None)
        # end::turbostreamrenderer[]
        assert writable.isPresent()
        writer = StringWriter()
        writable.get().writeTo(writer)
        result = writer.toString()

        assert result == ("<turbo-stream action=\"append\" target=\"dom_id\">"
                          "<template>"
                          "<h1>fruit: Banana</h1>\n"
                          "<h2>color: Yellow</h2>\n"
                          "</template>"
                          "</turbo-stream>")
