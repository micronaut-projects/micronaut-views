package io.micronaut.views.fields;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.StringUtils;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.fields.elements.InputHiddenFormElement;
import io.micronaut.views.fields.render.FormElementRenderer;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Property(name = "micronaut.views.form-element.render.enabled", value = StringUtils.FALSE)
@MicronautTest(startApplication = false)
class FormElementRenderDisabledTest {

    @Inject
    BeanContext beanContext;

    @Test
    void formElementRendererIsDisabled() {
        assertFalse(beanContext.containsBean(Argument.of(FormElementRenderer.class, InputHiddenFormElement.class)));
    }

}
