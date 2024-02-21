package io.micronaut.views.turbo

import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class TurboFrameFilterSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "by default a bean of type TurboFrameFilter exists"() {
        expect:
        beanContext.containsBean(TurboFrameFilter)
    }
}