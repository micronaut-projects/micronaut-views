package io.micronaut.views.react.dev

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Property
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.react.ReactRenderPostProcessor
import jakarta.inject.Inject
import spock.lang.Specification

/**
 * Outside the development environment none of this exists, even though the property asks for it.
 * The property alone must not be enough: a setting left in a committed config file should not turn
 * a production deployment into one serving an unauthenticated endpoint.
 */
@MicronautTest(startApplication = false, environments = ["prod"], rebuildContext = true)
@Property(name = "micronaut.views.react.dev.enabled", value = "true")
class DevReloadNotInProductionSpec extends Specification {
    @Inject
    ApplicationContext context

    void "nothing is registered outside dev"() {
        expect:
        !context.containsBean(ReactDevConfiguration)
        !context.containsBean(ReactRenderPostProcessor)
        !context.containsBean(ReactDevReloadController)
    }
}
