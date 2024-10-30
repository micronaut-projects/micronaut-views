package io.micronaut.views.model.security;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CsrfViewModelProcessorTest {
    @Test
    void beanOfTypeCsrfViewModelProcessorIsNotPresent() {
        try (ApplicationContext ctx = ApplicationContext.run(
                Map.of( "micronaut.security.csrf.views-model-decorator.enabled", StringUtils.FALSE)
        )) {
            assertFalse(ctx.containsBean(CsrfViewModelProcessor.class));
        }
    }
}