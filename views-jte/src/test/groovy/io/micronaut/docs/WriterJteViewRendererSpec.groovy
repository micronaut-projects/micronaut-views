package io.micronaut.docs

class WriterJteViewRendererSpec extends JteViewRendererSpec {
    @Override
    Map<String, Object> getTestProperties() {
        return [
                'spec.name': 'jte',
                'micronaut.security.enabled': false,
                'micronaut.views.jte.dynamic': true
        ] as Map<String, Object>
    }
}
