package io.micronaut.views.htmx;

import io.micronaut.context.BeanContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest(startApplication = false)
class HtmxConfigurationTest {

    @Inject
    BeanContext beanContext;

    @Test
    void byDefaultBeanOftypeHtmxConfigurationExists() {
        assertTrue(beanContext.containsBean(HtmxConfiguration.class));
    }

    @Test
    void canBeDisabledViaProperties() {
        try (ApplicationContext ctx = ApplicationContext.run(Map.of("micronaut.views.htmx.enabled", "false"))) {
            assertFalse(ctx.containsBean(HtmxConfiguration.class));
        }
    }
    @Test
    void canBeDisabledViaProperties() {
        try (ApplicationContext ctx = ApplicationContext.run(Map.of("micronaut.views.htmx.enabled", "false"))) {
            assertFalse(ctx.containsBean(HtmxConfiguration.class));
        }
    }
}