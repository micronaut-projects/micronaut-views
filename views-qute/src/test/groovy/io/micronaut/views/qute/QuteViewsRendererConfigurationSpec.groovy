/*
 * Copyright 2017-2026 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.qute

import io.micronaut.context.ApplicationContext
import io.micronaut.views.ViewsRenderer
import spock.lang.Specification

class QuteViewsRendererConfigurationSpec extends Specification {

    def "renderer is not loaded when disabled"() {
        given:
        ApplicationContext context = ApplicationContext.run([
                "micronaut.views.qute.enabled": false,
        ])

        expect:
        !context.containsBean(QuteViewsRenderer)
        !context.containsBean(ViewsRenderer)

        cleanup:
        context.close()
    }

    def "default extension can be configured"() {
        given:
        ApplicationContext context = ApplicationContext.run([
                "micronaut.views.qute.default-extension": "qute.html",
        ])
        QuteViewsRenderer renderer = context.getBean(QuteViewsRenderer)

        expect:
        renderer.exists("alternate")
        !renderer.exists("missing")

        cleanup:
        context.close()
    }
}
