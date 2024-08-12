package io.micronaut.views.react

import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.react.truffle.IntrospectableToTruffleAdapter
import io.micronaut.views.react.util.BeanPool
import jakarta.inject.Inject
import org.graalvm.polyglot.Value
import org.graalvm.polyglot.proxy.ProxyObject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class IntrospectableBeansAreProxiedSpec extends Specification {
    @Inject
    BeanPool contextPool

    void "introspectable bean can be proxied"() {
        given:
        BeanPool.Handle<JSContext> jsContext = contextPool.checkOut()
        def context = jsContext.get().polyglotContext
        def bean = new SomeBean("foo value", "bar value", new SomeBean.InnerBean(10, Map.of("key", 123), List.of("one", "two", "three")))

        when:
        ProxyObject proxy = IntrospectableToTruffleAdapter.wrap(context, bean).asProxyObject()
        context.getBindings("js").putMember("bean", proxy)

        then:
        proxy.getMember("foo") == context.asValue("foo value")
        proxy.getMember("innerBean") instanceof Value

        when:
        ProxyObject innerBean = ((Value) proxy.getMember("innerBean")).asProxyObject()

        then:
        innerBean instanceof IntrospectableToTruffleAdapter

        when:
        ProxyObject innerBeanMap = ((Value) innerBean.getMember("map")).asProxyObject()

        then:
        ((Value) innerBeanMap.getMember("key")).asInt() == 123
        context.eval("js", "bean.innerBean.map[\"key\"]").asInt() == 123
    }
}
