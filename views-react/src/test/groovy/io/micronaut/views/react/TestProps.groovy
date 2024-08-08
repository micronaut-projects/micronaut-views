package io.micronaut.views.react

class TestProps {
    static def basic = ["name": "Mike", "someNull": null, "obj": new SomeBean("foo", null)]
    static def triggerSandbox = basic + ["triggerSandbox": true]
}
