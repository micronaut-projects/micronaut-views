package io.micronaut.views

import spock.lang.Specification
import spock.lang.Unroll

class ViewUtilsSpec extends Specification {
    @Unroll
    def "normalizeFile handles extensions with and without ."(path, extension, expected) {
        when:
        def actual = ViewUtils.normalizeFile(path, extension)

        then:
        actual == expected

        where:
        path | extension | expected
        'home.xyz' | null | 'home.xyz'
        'home.xyz' | 'xyz' | 'home'
        'home.xyz' | '.xyz' | 'home'
    }
}
