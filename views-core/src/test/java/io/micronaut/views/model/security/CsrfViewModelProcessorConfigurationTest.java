package io.micronaut.views.model.security;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import org.junit.jupiter.api.Test;
import spock.lang.Specification;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CsrfViewModelProcessorConfigurationTest extends Specification {

    @Test
    void testCsrfViewModelProcessorConfiguration() {
        try (ApplicationContext ctx = ApplicationContext.run()) {
            CsrfViewModelProcessorConfiguration csrfViewModelProcessorConfiguration = ctx.getBean(CsrfViewModelProcessorConfiguration.class);
            assertEquals("csrfToken", csrfViewModelProcessorConfiguration.getCsrfTokenKey());
            assertTrue(csrfViewModelProcessorConfiguration.isEnabled());
        }
    }

    @Test
    void testCsrfViewModelProcessorConfigurationSetting() {
        try (ApplicationContext ctx = ApplicationContext.run(
                Map.of(
                        "micronaut.security.csrf.views-model-decorator.csrf-token-key", "foobar",
                        "micronaut.security.csrf.views-model-decorator.enabled", StringUtils.FALSE
        ))) {
            CsrfViewModelProcessorConfiguration csrfViewModelProcessorConfiguration = ctx.getBean(CsrfViewModelProcessorConfiguration.class);
            assertEquals("foobar", csrfViewModelProcessorConfiguration.getCsrfTokenKey());
            assertFalse(csrfViewModelProcessorConfiguration.isEnabled());
        }
    }
}