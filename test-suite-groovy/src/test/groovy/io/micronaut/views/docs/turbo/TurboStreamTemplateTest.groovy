package io.micronaut.views.docs.turbo

import io.micronaut.context.ApplicationContext
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.io.Writable
import io.micronaut.core.util.StringUtils
import io.micronaut.views.turbo.TurboStream
import io.micronaut.views.turbo.TurboStreamAction
import io.micronaut.views.turbo.TurboStreamRenderer
import spock.lang.Specification

class TurboStreamTemplateTest extends Specification {

    void "TurboRenderer allows you to render Templates"() throws IOException {
        given:
        ApplicationContext context = ApplicationContext.run(Collections.singletonMap("micronaut.views.soy.enabled", StringUtils.FALSE))

        TurboStreamRenderer turboStreamRenderer = context.getBean(TurboStreamRenderer.class)
        when:
//tag::turbostreamrenderer[]
String view = "fruit"
Map<String, Object> model = Collections.singletonMap("fruit", new Fruit("Banana", "Yellow"))
TurboStream.Builder builder = TurboStream.builder()
    .action(TurboStreamAction.APPEND)
    .targetDomId("dom_id")
    .template(view, model)
Optional<Writable> writable = turboStreamRenderer.render(builder, null)
//end::turbostreamrenderer[]
        then:
        writable.isPresent()

        when:
        StringWriter writer = new StringWriter()
        writable.get().writeTo(writer)
        String result = writer.toString()

        then:
                "<turbo-stream action=\"append\" target=\"dom_id\">"+
                        "<template>" +
                        "<h1>fruit: Banana</h1>\n" +
                        "<h2>color: Yellow</h2>" +
                        "</template>" +
                        "</turbo-stream>"
                == result
        cleanup:
        context.close()
    }

    @Introspected
    static class Fruit {
        private final String name
        private final String color

        Fruit(String name, String color) {
            this.name = name
            this.color = color
        }

        String getName() {
            return name
        }

        String getColor() {
            return color
        }
    }
}
