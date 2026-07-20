package io.micronaut.docs

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.micronaut.core.annotation.Introspected

@Canonical
@CompileStatic
@Introspected
class Person {
    String username
    boolean loggedIn
}
