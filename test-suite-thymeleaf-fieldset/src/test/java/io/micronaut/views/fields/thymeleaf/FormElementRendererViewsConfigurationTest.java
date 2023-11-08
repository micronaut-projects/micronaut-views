package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.BeanContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.render.views.FormElementRendererViewsConfiguration;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class FormElementRendererViewsConfigurationTest {

    @Inject
    BeanContext beanContext;

    @Test
    void beanOfTypeFormElementRendererViewsConfigurationExists() {
        assertTrue(beanContext.containsBean(FormElementRendererViewsConfiguration.class));
        FormElementRendererViewsConfiguration configuration = beanContext.getBean(FormElementRendererViewsConfiguration.class);
        assertNull(configuration.getInputCheckbox());
        assertNull(configuration.getInputCheckbox());
        assertNull(configuration.getInputDate());
        assertNull(configuration.getInputDateTimeLocal());
        assertNull(configuration.getInputEmail());
        assertNull(configuration.getInputHidden());
        assertNull(configuration.getInputNumber());
        assertNull(configuration.getInputPassword());
        assertNull(configuration.getInputRadio());
        assertNull(configuration.getInputSubmit());
        assertNull(configuration.getInputTel());
        assertNull(configuration.getInputText());
        assertNull(configuration.getInputTime());
        assertNull(configuration.getInputUrl());
        assertNull(configuration.getOption());
        assertNull(configuration.getSelect());
        assertNull(configuration.getTextarea());
        assertNull(configuration.getTrixEditor());
    }
}