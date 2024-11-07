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
package io.micronaut.views.react.truffle;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanIntrospector;
import io.micronaut.core.beans.BeanMap;
import io.micronaut.core.beans.BeanMethod;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A proxy object similar to that returned by {@link ProxyObject#fromMap(Map)}, but with support
 * for Micronaut's bean introspection system (a form of compile-time reflection code generation).
 * Reading a key whose value is an introspectable bean will use the {@link BeanMap} instead of
 * the regular polyglot mapping.
 */
@Internal
public final class IntrospectableToTruffleAdapter implements ProxyObject {
    private final Context context;
    private final Object target;

    @Nullable
    private final BeanIntrospection<?> introspection;

    private IntrospectableToTruffleAdapter(Context context, Object target, BeanIntrospection<?> introspection) {
        this.context = context;
        this.target = target;
        this.introspection = introspection;
    }

    /**
     * Wraps an object as a Truffle {@link Value} suitable for guest access, wrapping introspectable types with {@link IntrospectableToTruffleAdapter}.
     *
     * @param context The language context to wrap the object into.
     * @param object Either null, a {@link Map}, a {@link Collection}, an {@link io.micronaut.core.annotation.Introspected introspectable object}, or any other object supported by the Polyglot interop layer.
     * @return A value that will return true to {@link Value#isProxyObject()}
     */
    public static Value wrap(Context context, Object object) {
        if (object == null) {
            return context.asValue(null);
        } else  if (object instanceof Map<?, ?> map) {
            // We need to recursively map the values.
            var result = new HashMap<String, Object>();
            map.forEach((key, value) -> result.put(key.toString(), wrap(context, value)));
            return context.asValue(ProxyObject.fromMap(result));
        } else if (object instanceof Collection<?> collection) {
            // We need to recursively map the items. This could be lazy.
            return context.asValue(collection.stream().map(it -> wrap(context, it)).toList());
        } else if (object instanceof String) {
            // We could ignore this case because we'd fall through the BeanIntrospector check, but that logs some debug spam,
            // and it's slower to look up objects we know we won't wrap anyway.
            return context.asValue(object);
        } else {
            var introspection = BeanIntrospector.SHARED.findIntrospection(object.getClass()).orElse(null);
            if (introspection != null) {
                return context.asValue(new IntrospectableToTruffleAdapter(context, object, introspection));
            } else {
                return context.asValue(object);
            }
        }
    }

    @Override
    public Object getMember(String key) {
        Map<String, Object> map = asMap();

        // Is it a property?
        Object result = map.get(key);
        if (result != null) {
            return wrap(context, result);
        }

        // Can it be an @Executable method?
        if (introspection != null) {
            Collection<? extends BeanMethod<?, Object>> beanMethods = introspection.getBeanMethods();
            for (BeanMethod<?, Object> method : beanMethods) {
                if (method.getName().equals(key)) {
                    return new PolyglotBeanMethod(beanMethods);
                }
            }
        }

        // Not found.
        return context.asValue(null);
    }

    @Override
    public Object getMemberKeys() {
        return ProxyArray.fromList(getInvokableNames());
    }

    @Override
    public boolean hasMember(String key) {
        return getInvokableNames().contains(key);
    }

    private ArrayList<Object> getInvokableNames() {
        ArrayList<Object> propNames = new ArrayList<>(asMap().keySet());
        if (introspection != null) {
            introspection.getBeanMethods().forEach(it -> propNames.add(it.getName()));
        }
        return propNames;
    }

    @Override
    public void putMember(String key, Value value) {
        throw new UnsupportedOperationException();
    }

    private Map<String, Object> asMap() {
        return BeanMap.of(target);
    }

    @Override
    public String toString() {
        return target.toString();
    }

    @SuppressWarnings("rawtypes")
    private final class PolyglotBeanMethod implements ProxyExecutable {
        private final Collection<? extends BeanMethod<?, Object>> candidates;

        private PolyglotBeanMethod(Collection<? extends BeanMethod<?, Object>> candidates) {
            assert !candidates.isEmpty();
            this.candidates = candidates;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object execute(Value... arguments) {
            BeanMethod candidate = findCandidateByNumberOfArguments(arguments);
            Object[] convertedArgs = new Object[arguments.length];
            for (int i = 0; i < arguments.length; i++) {
                convertedArgs[i] = arguments[i].as(Object.class);
            }
            return context.asValue(candidate.invoke(target, convertedArgs));
        }

        private BeanMethod findCandidateByNumberOfArguments(Value[] arguments) {
            int minNeeded = Integer.MAX_VALUE;
            for (BeanMethod candidate : candidates) {
                int numArgs = candidate.getArguments().length;
                minNeeded = Math.min(minNeeded, numArgs);
                if (numArgs == arguments.length) {
                    return candidate;
                }
            }
            throw new UnsupportedOperationException(String.format("No candidates found with the right number of arguments for method %s, needed at least %d but got %d", candidates.iterator().next().getName(), minNeeded, arguments.length));
        }
    }
}
