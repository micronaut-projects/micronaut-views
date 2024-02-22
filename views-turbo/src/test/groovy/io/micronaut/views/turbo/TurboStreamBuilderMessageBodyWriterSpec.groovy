package io.micronaut.views.turbo

import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class TurboStreamBuilderMessageBodyWriterSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "by default a bean of type TurboStreamBuilderMessageBodyWriter exists"() {
        expect:
        beanContext.containsBean(TurboStreamBuilderMessageBodyWriter)
    }
}