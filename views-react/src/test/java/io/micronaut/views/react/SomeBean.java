package io.micronaut.views.react;

import io.micronaut.context.annotation.Executable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Test bean accessed from inside the sandbox.
 */
@Introspected
public class SomeBean {
    private final String foo;
    private final @Nullable String bar;
    private final InnerBean innerBean;

    SomeBean(String foo, String bar, InnerBean innerBean) {
        this.foo = foo;
        this.bar = bar;
        this.innerBean = innerBean;
    }

    public String getFoo() {
        return foo;
    }

    public String getBar() {
        return bar;
    }

    public InnerBean getInnerBean() {
        return innerBean;
    }

    @Executable
    public String sayGoodbye(String name) {
        return "Goodbye " + name + "!";
    }

    @Introspected
    public static class InnerBean {
        private final int a;
        private final Map<Object, Object> map;
        private final List<String> list;

        InnerBean(int a, Map<Object, Object> map, List<String> list) {
            this.a = a;
            this.map = map;
            this.list = list;
        }

        public Map<Object, Object> getMap() {
            return map;
        }

        public int getA() {
            return a;
        }

        public List<String> getList() {
            return list;
        }
    }
}
