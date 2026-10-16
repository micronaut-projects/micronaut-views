package io.micronaut.views.jinjava;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.io.Writable;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JinjavaViewsRendererAutoescapeTest {

    private static final Map<String, Object> MODEL = Map.of("message", "<b>x</b>");

    @Test
    void autoescapeIsDisabledByDefault() throws IOException {
        try (ApplicationContext context = ApplicationContext.run()) {
            assertFalse(context.getBean(JinjavaViewsRendererConfigurationProperties.class).isAutoescape());
            String html = render(context);

            assertTrue(html.contains("<h1><b>x</b></h1>"), html);
            assertFalse(html.contains("&lt;b&gt;"), html);
        }
    }

    @Test
    void autoescapeEscapesExpressionOutputWhenEnabled() throws IOException {
        try (ApplicationContext context = ApplicationContext.run(Map.of("micronaut.views.jinja.autoescape", true))) {
            assertTrue(context.getBean(JinjavaViewsRendererConfigurationProperties.class).isAutoescape());
            String html = render(context);

            assertTrue(html.contains("<h1>&lt;b&gt;x&lt;/b&gt;</h1>"), html);
            assertFalse(html.contains("<b>"), html);
        }
    }

    @Test
    void autoescapeCanBeExplicitlyDisabled() throws IOException {
        try (ApplicationContext context = ApplicationContext.run(Map.of("micronaut.views.jinja.autoescape", false))) {
            assertEquals("<h1><b>x</b></h1>", render(context).trim());
        }
    }

    @Test
    void safeFilterAndAutoescapeBlockOptOutAndEscapeFilterDoubleEscapes() throws IOException {
        try (ApplicationContext context = ApplicationContext.run(Map.of("micronaut.views.jinja.autoescape", true))) {
            String html = render(context, "autoescape-filters");

            assertTrue(html.contains("safe=[<b>x</b>]"), html);
            assertTrue(html.contains("block=[<b>x</b>]"), html);
            assertTrue(html.contains("escape=[&amp;lt;b&amp;gt;x&amp;lt;/b&amp;gt;]"), html);
        }
    }

    private static String render(ApplicationContext context) throws IOException {
        return render(context, "autoescape");
    }

    @SuppressWarnings("unchecked")
    private static String render(ApplicationContext context, String view) throws IOException {
        JinjavaViewsRenderer<Object, Object> renderer = context.getBean(JinjavaViewsRenderer.class);
        Writable writable = renderer.render(view, MODEL, null);
        StringWriter writer = new StringWriter();
        writable.writeTo(writer);
        return writer.toString();
    }
}
