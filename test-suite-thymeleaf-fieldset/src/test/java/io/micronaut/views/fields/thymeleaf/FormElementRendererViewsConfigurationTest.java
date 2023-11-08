package io.micronaut.views.fields.thymeleaf;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.render.views.FormElementRendererViewsConfiguration;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class FormElementRendererViewsConfigurationTest {

    @Test
    void beanOfTypeFormElementRendererViewsConfigurationExists() {
        ApplicationContext ctx = ApplicationContext.run();
        assertTrue(ctx.containsBean(FormElementRendererViewsConfiguration.class));
        FormElementRendererViewsConfiguration configuration = ctx.getBean(FormElementRendererViewsConfiguration.class);
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
        ctx.close();
    }

    @Test
    void FormElementRendererViewsConfigurationCanBeSetViaConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("micronaut.views.form-element.render.views.input-tel", "fieldset/inputtel.html");
        config.put("micronaut.views.form-element.render.views.input-checkbox","fieldset/inputcheckbox.html");
        config.put("micronaut.views.form-element.render.views.input-date", "fieldset/inputdate.html");
        config.put("micronaut.views.form-element.render.views.input-date-time-local", "fieldset/inputdatetimelocal.html");
        config.put("micronaut.views.form-element.render.views.input-email", "fieldset/inputemail.html");
        config.put("micronaut.views.form-element.render.views.input-hidden","fieldset/inputhidden.html");
        config.put("micronaut.views.form-element.render.views.input-number", "fieldset/inputnumber.html");
        config.put("micronaut.views.form-element.render.views.input-password", "fieldset/inputpassword.html");
        config.put("micronaut.views.form-element.render.views.input-radio", "fieldset/inputradios.html");
        config.put("micronaut.views.form-element.render.views.input-submit", "fieldset/inputsubmit.html");
        config.put("micronaut.views.form-element.render.views.input-text", "fieldset/inputtext.html");
        config.put("micronaut.views.form-element.render.views.input-time", "fieldset/inputtime.html");
        config.put("micronaut.views.form-element.render.views.input-url", "fieldset/inputurl.html");
        config.put("micronaut.views.form-element.render.views.option", "fieldset/option.html");
        config.put("micronaut.views.form-element.render.views.select", "fieldset/select.html");
        config.put("micronaut.views.form-element.render.views.textarea", "fieldset/textarea.html");
        config.put("micronaut.views.form-element.render.views.trix-editor", "fieldset/trixeditor.html");
        ApplicationContext ctx = ApplicationContext.run(config);
        assertTrue(ctx.containsBean(FormElementRendererViewsConfiguration.class));
        FormElementRendererViewsConfiguration configuration = ctx.getBean(FormElementRendererViewsConfiguration.class);
        assertEquals("fieldset/inputcheckbox.html", configuration.getInputCheckbox());
        assertEquals("fieldset/inputdate.html", configuration.getInputDate());
        assertEquals("fieldset/inputdatetimelocal.html", configuration.getInputDateTimeLocal());
        assertEquals("fieldset/inputemail.html", configuration.getInputEmail());
        assertEquals("fieldset/inputhidden.html", configuration.getInputHidden());
        assertEquals("fieldset/inputnumber.html", configuration.getInputNumber());
        assertEquals("fieldset/inputpassword.html", configuration.getInputPassword());
        assertEquals("fieldset/inputradios.html", configuration.getInputRadio());
        assertEquals("fieldset/inputsubmit.html", configuration.getInputSubmit());
        assertEquals("fieldset/inputtel.html", configuration.getInputTel());
        assertEquals("fieldset/inputtext.html", configuration.getInputText());
        assertEquals("fieldset/inputtime.html", configuration.getInputTime());
        assertEquals("fieldset/inputurl.html", configuration.getInputUrl());
        assertEquals("fieldset/option.html", configuration.getOption());
        assertEquals("fieldset/select.html", configuration.getSelect());
        assertEquals("fieldset/textarea.html", configuration.getTextarea());
        assertEquals("fieldset/trixeditor.html", configuration.getTrixEditor());
        ctx.close();
    }
}