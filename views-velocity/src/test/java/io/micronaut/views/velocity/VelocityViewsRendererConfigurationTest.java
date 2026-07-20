package io.micronaut.views.velocity;

import io.micronaut.context.ApplicationContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.ViewsRendererConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class VelocityViewsRendererConfigurationTest {

    @Test
    void testNamedViewRendererConfiguration() {
        try (ApplicationContext context = ApplicationContext.run()) {
            ViewsRendererConfiguration configuration = context.getBean(ViewsRendererConfiguration.class, Qualifiers.byName("velocity"));

            assertInstanceOf(VelocityViewsRendererConfiguration.class, configuration);
        }
    }
}
