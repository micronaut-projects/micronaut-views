package io.micronaut.views.htmx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HtmxConfigurationPropertiesTest {

    @Test
    void settersAndGetters() {
        HtmxConfigurationProperties htmxConfigurationProperties = new HtmxConfigurationProperties();
        htmxConfigurationProperties.setEnabled(true);
        assertTrue(htmxConfigurationProperties.isEnabled());
    }
}