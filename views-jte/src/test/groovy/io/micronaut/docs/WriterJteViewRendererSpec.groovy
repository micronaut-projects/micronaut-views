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

    def "foo /jte/modelAndView renders jte template from a controller returning a ModelAndView instance"() {
        when:
        HttpResponse<String> rsp = client.toBlocking().exchange('/jte/modelAndView', String)

        then:
        noExceptionThrown()
        rsp.status() == HttpStatus.OK

        when:
        String body = rsp.body()

        then:
        body
        rsp.body().contains("<h1>username: <span>sdelamo</span></h1>")
    }
}
