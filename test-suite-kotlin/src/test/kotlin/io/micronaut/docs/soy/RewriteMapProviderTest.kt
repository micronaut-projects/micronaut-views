package io.micronaut.docs.soy

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.views.soy.SoyNamingMapProvider
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


@Property(name = "spec.name", value = "RewriteMapProviderTest")
@MicronautTest
internal class RewriteMapProviderTest {

    @Inject
    lateinit var beanContext: BeanContext

    @Test
    fun testReadRenamingMapFile() {
        val rewriteMapProvider = beanContext.getBean(SoyNamingMapProvider::class.java)

        Assertions.assertNotNull(rewriteMapProvider)
        val soyCssRenamingMap = rewriteMapProvider.cssRenamingMap()
        Assertions.assertEquals("a", soyCssRenamingMap["dialog"])
        Assertions.assertEquals("b", soyCssRenamingMap["content"])
        Assertions.assertEquals("c", soyCssRenamingMap["title"] )
    }
}

