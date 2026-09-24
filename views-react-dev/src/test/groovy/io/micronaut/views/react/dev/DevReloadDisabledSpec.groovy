package io.micronaut.views.react.dev

import io.micronaut.context.ApplicationContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.react.ReactRenderPostProcessor
import jakarta.inject.Inject
import spock.lang.Specification

/**
 * In dev, but not asked for. Being on the classpath is not consent: nothing is registered, so
 * nothing is injected and no endpoint is routable.
 */
@MicronautTest(startApplication = false, environments = ["dev"], rebuildContext = true)
class DevReloadDisabledSpec extends Specification {
    @Inject
    ApplicationContext context

    void "nothing is registered unless enabled"() {
        expect:
        !context.containsBean(ReactDevConfiguration)
        !context.containsBean(ReactRenderPostProcessor)
        !context.containsBean(ReactDevReloadController)
        !context.containsBean(ReactDevReloadBroadcaster)
    }
}
