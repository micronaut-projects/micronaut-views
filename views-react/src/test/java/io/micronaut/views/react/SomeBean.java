package io.micronaut.views.react;

import io.micronaut.context.annotation.Executable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import org.graalvm.polyglot.HostAccess;

/**
 * Test bean accessed from inside the sandbox.
 */
@Introspected
public class SomeBean {
    private final String foo;
    private final @Nullable String bar;

    SomeBean(String foo, String bar) {
        this.foo = foo;
        this.bar = bar;
    }

    @HostAccess.Export
    public String getFoo() {
        return foo;
    }

    @HostAccess.Export
    public String getBar() {
        return bar;
    }

    @HostAccess.Export
    @Executable
    public String sayGoodbye(String name) {
        return "Goodbye " + name + "!";
    }
}
