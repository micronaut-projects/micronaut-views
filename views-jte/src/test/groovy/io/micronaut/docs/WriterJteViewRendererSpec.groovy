package io.micronaut.docs

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus

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
