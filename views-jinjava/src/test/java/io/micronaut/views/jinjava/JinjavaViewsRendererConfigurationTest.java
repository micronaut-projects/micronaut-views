package io.micronaut.views.jinjava;

import com.hubspot.jinjava.Jinjava;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.io.Writable;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
@Property(name = "micronaut.views.jinja.config.trim-blocks", value = "true")
class JinjavaViewsRendererConfigurationTest {

    @Test
    void bindsJinjavaBuilderSettings(Jinjava jinjava) {
        assertTrue(jinjava.getGlobalConfig().isTrimBlocks());
    }

    @Test
    void rendersWithNoRequestAndOnlyLoadsExistingViews(JinjavaViewsRenderer<Object, Object> renderer) throws IOException {
        Writable writable = renderer.render("tim", Map.of("username", "Tim"), null);
        StringWriter writer = new StringWriter();
        writable.writeTo(writer);

        assertTrue(writer.toString().contains("username: <span>Tim</span>"));
        assertTrue(renderer.exists("tim"));
        assertFalse(renderer.exists(""));
        assertFalse(renderer.exists("../application.yml"));
        assertFalse(renderer.exists("missing"));
    }

    @Test
    void canBeDisabled() {
        try (ApplicationContext context = ApplicationContext.run(Map.of("micronaut.views.jinja.enabled", false))) {
            assertFalse(context.containsBean(JinjavaViewsRenderer.class));
        }
    }
}
