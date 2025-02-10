package io.micronaut.views.turbo

import spock.lang.Specification
import spock.lang.Unroll

class VisitActionSpec extends Specification {

    @Unroll
    void "VisitAction::toString() returns the action in lowercase"(VisitAction action, String expected) {
        expect:
        expected == action.toString()
        expected == action.getAction()

        where:
        action               || expected
        VisitAction.RESTORE  || 'restore'
        VisitAction.ADVANCE  || 'advance'
        VisitAction.REPLACE  || 'replace'
    }
}
