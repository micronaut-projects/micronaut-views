/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 *
 * @param <T> The type of the introspectable bean.
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
        if (readOnly) {
            throw new IllegalStateException("You cannot write to this object; it is marked read only from Java.");
        }
        beanMap.put(key, value.asHostObject());
    }
}
