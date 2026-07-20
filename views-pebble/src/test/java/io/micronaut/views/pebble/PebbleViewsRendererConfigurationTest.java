package io.micronaut.views.pebble;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.ViewsRendererConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PebbleViewsRendererConfigurationTest {

    @Test
    void testNamedViewRendererConfiguration() {
        try (ApplicationContext context = ApplicationContext.run()) {
            ViewsRendererConfiguration configuration = context.getBean(ViewsRendererConfiguration.class, Qualifiers.byName("pebble"));

            assertInstanceOf(PebbleConfiguration.class, configuration);
        }
    }
}
