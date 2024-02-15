package io.micronaut.views.docs.turbo;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamAction;
import io.micronaut.views.turbo.TurboStreamRenderer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurboStreamTemplateTest {

    @Test
    void turboRendererAllowsYouToRenderTemplates() throws IOException {
        ApplicationContext context = ApplicationContext.run(Collections.singletonMap("micronaut.views.soy.enabled", StringUtils.FALSE));

        TurboStreamRenderer turboStreamRenderer = context.getBean(TurboStreamRenderer.class);
        //tag::turbostreamrenderer[]
        String view = "fruit";
        Map<String, Object> model = Collections.singletonMap("fruit", new Fruit("Banana", "Yellow"));
        TurboStream.Builder builder = TurboStream.builder()
            .action(TurboStreamAction.APPEND)
            .targetDomId("dom_id")
            .template(view, model);
        Optional<Writable> writable = turboStreamRenderer.render(builder, null);
        //end::turbostreamrenderer[]
        assertTrue(writable.isPresent());
        StringWriter writer = new StringWriter();
        writable.get().writeTo(writer);
        String result = writer.toString();

        assertEquals(
                "<turbo-stream action=\"append\" target=\"dom_id\">"+
                        "<template>" +
                        "<h1>fruit: Banana</h1>\n" +
                        "<h2>color: Yellow</h2>\n" +
                        "</template>" +
                        "</turbo-stream>"
                , result);
        context.close();
    }

}
