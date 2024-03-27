package io.micronaut.views.react;

import io.micronaut.core.beans.BeanIntrospector;
import io.micronaut.core.beans.BeanMap;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyObject;

/**
 * A wrapper that takes an {@code @Introspectable} object (bean) and lazily proxies it into a Polyglot language
 * context.
 */
class IntrospectableToPolyglotObject<T> implements ProxyObject {
    private final Context context;
    private final boolean readOnly;
    private final BeanMap<T> beanMap;

    IntrospectableToPolyglotObject(Context context, boolean readOnly, T object) {
        this.context = context;
        this.readOnly = readOnly;
        beanMap = BeanMap.of(object);
    }

    @Override
    public Object getMember(String key) {
        Object result = beanMap.get(key);
        if (BeanIntrospector.SHARED.findIntrospection(result.getClass()).isPresent()) {
            return new IntrospectableToPolyglotObject<>(context, readOnly, result);
        } else {
            return context.asValue(result);
        }
    }

    @Override
    public Object getMemberKeys() {
        Object[] fieldKeys = beanMap.keySet().toArray();

        return ProxyArray.fromArray(fieldKeys);
    }

    @Override
    public boolean hasMember(String key) {
        return beanMap.containsKey(key);
    }

    @Override
    public void putMember(String key, Value value) {
        if (readOnly)
            throw new IllegalStateException("You cannot write to this object; it is marked read only from Java.");
        beanMap.put(key, value.asHostObject());
    }
}
