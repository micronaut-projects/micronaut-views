package io.micronaut.views.turbo

import spock.lang.Specification
import spock.lang.Unroll

class TurboStreamActionSpec extends Specification {

    @Unroll
    void "TurboStreamAction::toString() returns the action in lowercase"(TurboStreamAction action, String expected) {
        expect:
        expected == action.toString()
        expected == action.getAction()

        where:
        action                    || expected
        TurboStreamAction.AFTER   || 'after'
        TurboStreamAction.BEFORE  || 'before'
        TurboStreamAction.REPLACE || 'replace'
        TurboStreamAction.PREPEND || 'prepend'
        TurboStreamAction.APPEND || 'append'
        TurboStreamAction.UPDATE || 'update'
        TurboStreamAction.REMOVE || 'remove'
    }
}
