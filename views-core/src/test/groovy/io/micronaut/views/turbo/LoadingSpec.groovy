package io.micronaut.views.turbo

import spock.lang.Specification
import spock.lang.Unroll

class LoadingSpec extends Specification {

    @Unroll
    void "Loading::toString() returns the loading in lowercase"(Loading loading, String expected) {
        expect:
        expected == loading.toString()
        expected == loading.getLoading()

        where:
        loading       || expected
        Loading.EAGER || 'eager'
        Loading.LAZY  || 'lazy'
    }
}
