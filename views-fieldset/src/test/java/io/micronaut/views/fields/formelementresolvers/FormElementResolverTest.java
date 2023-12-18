package io.micronaut.views.fields.formelementresolvers;

import io.micronaut.context.BeanContext;
import io.micronaut.core.order.OrderUtil;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest(startApplication = false)
class FormElementResolverTest {

    @Inject
    BeanContext beanContext;

    @Test
    void defaultFormElementResolverFirst() {
        List<FormElementResolver> resolvers = new ArrayList<>(beanContext.getBeansOfType(FormElementResolver.class));
        OrderUtil.sort(resolvers);
        assertEquals(3, resolvers.size());
        assertTrue(resolvers.get(0) instanceof CompositeFormElementResolver);
        assertTrue(resolvers.get(1) instanceof DefaultFormElementResolver);
        assertTrue(resolvers.get(2) instanceof CompletedFileUploadFormElementResolver);
    }
}