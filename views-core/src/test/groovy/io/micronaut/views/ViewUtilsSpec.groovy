package io.micronaut.views

import spock.lang.Specification

class ViewUtilsSpec extends Specification {

    def "ViewsUtils.normalizedPath(#path,#extension) == #expected"() {
        expect:
        expected == ViewUtils.normalizeFile(path, extension)

        where:
        path       | extension || expected
        'home.xyz' | null      || 'home.xyz'
        'home.xyz' | 'xyz'     || 'home'
        'home.xyz' | '.xyz'    || 'home'
    }
}
