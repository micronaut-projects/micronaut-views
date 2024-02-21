package io.micronaut.views

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.turbo.TurboFrameBuilderHtmlMessageBodyWriter
import io.micronaut.views.turbo.TurboFrameFilter
import io.micronaut.views.turbo.TurboFrameRenderer
import io.micronaut.views.turbo.TurboStreamBuilderMessageBodyWriter
import io.micronaut.views.turbo.TurboStreamFilter
import io.micronaut.views.turbo.TurboStreamListOfBuilderMessageBodyWriter
import io.micronaut.views.turbo.TurboStreamMessageBodyWriter
import io.micronaut.views.turbo.TurboStreamRenderer
import jakarta.inject.Inject
import spock.lang.Specification


@Property(name = "micronaut.views.turbo.enabled", value = StringUtils.FALSE)
@MicronautTest
class TurboDisableSpec extends Specification {
    @Inject
    BeanContext beanContext

    void "with turbo disabled no bean of type TurboStreamRenderer exists"() {
        expect:
        !beanContext.containsBean(TurboStreamRenderer)
    }

    void "with turbo disabled no bean of type TurboStreamMessageBodyWriter exists"() {
        expect:
        !beanContext.containsBean(TurboStreamMessageBodyWriter)
    }

    void "with turbo disabled no bean of type TurboFrameBuilderHtmlMessageBodyWriter exists"() {
        expect:
        !beanContext.containsBean(TurboFrameBuilderHtmlMessageBodyWriter)
    }

    void "with turbo disabled no bean of type TurboStreamBuilderMessageBodyWriter exists"() {
        expect:
        !beanContext.containsBean(TurboStreamBuilderMessageBodyWriter)
    }

    void "with turbo disabled no bean of type TurboStreamListOfBuilderMessageBodyWriter exists"() {
        expect:
        !beanContext.containsBean(TurboStreamListOfBuilderMessageBodyWriter)
    }

    void "with turbo disabled no bean of type TurboFrameFilter exists"() {
        expect:
        !beanContext.containsBean(TurboFrameFilter)
    }

    void "with turbo disabled no bean of type TurboStreamFilter exists"() {
        expect:
        !beanContext.containsBean(TurboStreamFilter)
    }

    void "with turbo disabled no bean of type TurboFrameRenderer exists"() {
        expect:
        !beanContext.containsBean(TurboFrameRenderer)
    }
}
