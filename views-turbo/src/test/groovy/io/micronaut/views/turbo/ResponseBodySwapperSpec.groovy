package io.micronaut.views.turbo

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.http.CompositeResponseBodySwapper
import io.micronaut.views.http.ModelAndViewResponseBodySwapper
import io.micronaut.views.http.ResponseBodySwapper
import io.micronaut.views.turbo.http.bodyswapper.TurboFrameResponseBodySwapper
import io.micronaut.views.turbo.http.bodyswapper.TurboStreamResponseBodySwapper
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest
class ResponseBodySwapperSpec extends Specification  {

    @Inject
    List<ResponseBodySwapper> responseBodyResolverList

    void "responseBodyResolverList are ordered"() {
        expect:
        responseBodyResolverList.get(0) instanceof CompositeResponseBodySwapper
        responseBodyResolverList.get(1) instanceof TurboStreamResponseBodySwapper
        responseBodyResolverList.get(2) instanceof TurboFrameResponseBodySwapper
        responseBodyResolverList.get(3) instanceof ModelAndViewResponseBodySwapper
    }
}
