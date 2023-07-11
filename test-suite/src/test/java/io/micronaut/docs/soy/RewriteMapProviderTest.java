package io.micronaut.docs.soy;

import com.google.template.soy.shared.SoyCssRenamingMap;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.soy.SoyNamingMapProvider;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Property(name = "spec.name", value = "RewriteMapProviderTest")
@MicronautTest
class RewriteMapProviderTest {

    @Inject
    BeanContext beanContext;

    @Test
    void testReadRenamingMapFile() {
        SoyNamingMapProvider rewriteMapProvider = beanContext.getBean(SoyNamingMapProvider.class);
        assertNotNull(rewriteMapProvider);
        SoyCssRenamingMap soyCssRenamingMap = rewriteMapProvider.cssRenamingMap();
        assertEquals(soyCssRenamingMap.get("dialog"),"a");
        assertEquals(soyCssRenamingMap.get("content"),"b");
        assertEquals(soyCssRenamingMap.get("title"),"c");
    }
}
