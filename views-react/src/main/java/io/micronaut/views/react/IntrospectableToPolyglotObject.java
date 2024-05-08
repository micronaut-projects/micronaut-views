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

import java.util.Map;

/**
 * A proxy object similar to that returned by {@link ProxyObject#fromMap(Map)}, but with support
 * for Micronaut's bean introspection system (a form of compile-time reflection code generation).
 * Reading a key whose value is an introspectable bean will use the {@link BeanMap} instead of
 * the regular polyglot mapping.
 */
class ProxyObjectWithIntrospectableSupport implements ProxyObject {
    private final Context context;
    private final Map<String, Object> map;

    ProxyObjectWithIntrospectableSupport(Context context, Map<String, Object> map) {
        this.context = context;
        this.map = map;
    }

    @Override
    public Object getMember(String key) {
        Object result = map.get(key);
        if (BeanIntrospector.SHARED.findIntrospection(result.getClass()).isPresent()) {
            return new ProxyObjectWithIntrospectableSupport(context, BeanMap.of(result));
        } else {
            return context.asValue(result);
        }
    }

    @Override
    public Object getMemberKeys() {
        return ProxyArray.fromArray(map.keySet().toArray());
    }

    @Override
    public boolean hasMember(String key) {
        return map.containsKey(key);
    }

    @Override
    public void putMember(String key, Value value) {
        throw new UnsupportedOperationException();
    }
}
