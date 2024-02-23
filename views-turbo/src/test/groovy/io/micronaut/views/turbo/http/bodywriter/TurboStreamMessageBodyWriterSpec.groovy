package io.micronaut.views.turbo.http.bodywriter

import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class TurboStreamMessageBodyWriterSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "by default a bean of type TurboStreamMessageBodyWriter exists"() {
        expect:
        beanContext.containsBean(TurboStreamMessageBodyWriter)
    }
}