package io.micronaut.views.react;

import io.micronaut.core.annotation.Introspected;
import org.graalvm.polyglot.HostAccess;

/**
 * Test bean accessed from inside the sandbox.
 */
@Introspected
public class SomeBean {
    private final String foo;

    SomeBean(String foo) {
        this.foo = foo;
    }

    @HostAccess.Export
    public String getFoo() {
        return foo;
    }
}
