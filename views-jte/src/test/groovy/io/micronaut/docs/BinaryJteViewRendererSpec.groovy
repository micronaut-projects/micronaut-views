package io.micronaut.docs

import spock.lang.IgnoreIf

@IgnoreIf({!env["CI"]})
class BinaryJteViewRendererSpec extends JteViewRendererSpec {
    @Override
    Map<String, Object> getTestProperties() {
        return [
                'spec.name': 'jte',
                'micronaut.security.enabled': false,
                'micronaut.views.jte.dynamic': true,
                'micronaut.views.jte.binaryStaticContent': true
        ] as Map<String, Object>
    }
}
