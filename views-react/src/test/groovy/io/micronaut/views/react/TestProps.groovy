package io.micronaut.views.react

class TestProps {
    static def basic = ["name": "Mike", "someNull": null, "obj": new SomeBean("foo", null, new SomeBean.InnerBean(10, Map.of(), List.of("one", "two")))]
    static def triggerSandbox = basic + ["triggerSandbox": true]
}
