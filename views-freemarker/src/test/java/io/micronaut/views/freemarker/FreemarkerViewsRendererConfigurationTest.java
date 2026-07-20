package io.micronaut.views.freemarker;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ViewsRendererConfiguration;
import jakarta.inject.Named;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@MicronautTest(startApplication = false)
class FreemarkerViewsRendererConfigurationTest {

    @Test
    void testNamedViewRendererConfiguration(@Named("freemarker") ViewsRendererConfiguration config) {
        assertInstanceOf(FreemarkerViewsRendererConfiguration.class, config);
    }
}
