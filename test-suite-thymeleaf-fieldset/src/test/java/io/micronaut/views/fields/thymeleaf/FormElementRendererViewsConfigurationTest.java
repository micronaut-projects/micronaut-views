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
        assertNull(configuration.inputCheckbox());
        assertNull(configuration.inputDate());
        assertNull(configuration.inputDateTimeLocal());
        assertNull(configuration.inputEmail());
        assertNull(configuration.inputHidden());
        assertNull(configuration.inputNumber());
        assertNull(configuration.inputPassword());
        assertNull(configuration.inputRadio());
        assertNull(configuration.inputSubmit());
        assertNull(configuration.inputTel());
        assertNull(configuration.inputText());
        assertNull(configuration.inputTime());
        assertNull(configuration.inputUrl());
        assertNull(configuration.option());
        assertNull(configuration.select());
        assertNull(configuration.textarea());
        assertNull(configuration.trixEditor());
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
        assertEquals("fieldset/inputcheckbox.html", configuration.inputCheckbox());
        assertEquals("fieldset/inputdate.html", configuration.inputDate());
        assertEquals("fieldset/inputdatetimelocal.html", configuration.inputDateTimeLocal());
        assertEquals("fieldset/inputemail.html", configuration.inputEmail());
        assertEquals("fieldset/inputhidden.html", configuration.inputHidden());
        assertEquals("fieldset/inputnumber.html", configuration.inputNumber());
        assertEquals("fieldset/inputpassword.html", configuration.inputPassword());
        assertEquals("fieldset/inputradios.html", configuration.inputRadio());
        assertEquals("fieldset/inputsubmit.html", configuration.inputSubmit());
        assertEquals("fieldset/inputtel.html", configuration.inputTel());
        assertEquals("fieldset/inputtext.html", configuration.inputText());
        assertEquals("fieldset/inputtime.html", configuration.inputTime());
        assertEquals("fieldset/inputurl.html", configuration.inputUrl());
        assertEquals("fieldset/option.html", configuration.option());
        assertEquals("fieldset/select.html", configuration.select());
        assertEquals("fieldset/textarea.html", configuration.textarea());
        assertEquals("fieldset/trixeditor.html", configuration.trixEditor());
        ctx.close();
    }
}