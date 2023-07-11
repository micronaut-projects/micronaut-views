package io.micronaut.docs.soy

import com.google.template.soy.shared.SoyCssRenamingMap
import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.soy.SoyNamingMapProvider
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "spec.name", value = "RewriteMapProviderSpec")
@MicronautTest
class RewriteMapProviderSpec extends Specification {

    @Inject
    BeanContext beanContext;

    def "test read renaming map file"() {
        when:
        SoyNamingMapProvider rewriteMapProvider = beanContext.getBean(SoyNamingMapProvider.class);

        then:
        rewriteMapProvider
        SoyCssRenamingMap soyCssRenamingMap = rewriteMapProvider.cssRenamingMap();
        soyCssRenamingMap.get('dialog') == 'a'
        soyCssRenamingMap.get('content') == 'b'
        soyCssRenamingMap.get('title') == 'c'
    }
}
