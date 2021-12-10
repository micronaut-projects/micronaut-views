package io.micronaut.views

import spock.lang.Specification
import spock.lang.Unroll

class ViewUtilsSpec extends Specification {
    @Unroll("ViewsUtils.normalizedPath(#path,#extension) == #expected")
    def "normalizeFile handles extensions with and without ."(path, extension, expected) {
        expect:
        expected == ViewUtils.normalizeFile(path, extension)

        where:
        path       | extension || expected
        'home.xyz' | null      || 'home.xyz'
        'home.xyz' | 'xyz'     || 'home'
        'home.xyz' | '.xyz'    || 'home'
    }
}
