package io.micronaut.views.fields.tck;

import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewsRenderer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public final class TestUtils {
    private TestUtils() {
    }
    public static String render(String viewName, ViewsRenderer<Map<String, Object>, ?> viewsRenderer, Map<String, Object> model) throws IOException {
        return output(viewsRenderer.render(viewName, model, null));
    }

    public static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }
}
