package io.micronaut.views.http

import io.micronaut.context.ApplicationContext
import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class FilterSpec extends Specification {

    @Inject
    BeanContext beanContext

    void 'filter is enabled by default'() {
        expect:
        beanContext.containsBean(ResponseBodySwapperFilter)
    }

    void 'but can be disabled'() {
        given:
        def ctx = ApplicationContext.run(
                'micronaut.views.filter.enabled': false
        )

        expect:
        !ctx.containsBean(ResponseBodySwapperFilter)

        cleanup:
        ctx.close()
    }
}
