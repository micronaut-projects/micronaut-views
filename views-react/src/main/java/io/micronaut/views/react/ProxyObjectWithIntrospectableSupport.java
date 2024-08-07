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
import java.util.Map;

/**
 * A proxy object similar to that returned by {@link ProxyObject#fromMap(Map)}, but with support
 * for Micronaut's bean introspection system (a form of compile-time reflection code generation).
 * Reading a key whose value is an introspectable bean will use the {@link BeanMap} instead of
 * the regular polyglot mapping.
 */
@Internal
class ProxyObjectWithIntrospectableSupport implements ProxyObject {
    private final Context context;
    private final Object target;
    private final boolean isStringMap;

    @Nullable
    private final BeanIntrospection<?> introspection;

    ProxyObjectWithIntrospectableSupport(Context context, Object targetObject) {
        this.context = context;

        if (targetObject == null) {
            throw new NullPointerException("Cannot proxy a null");
        }

        this.target = targetObject;
        this.isStringMap = isStringMap(targetObject);
        this.introspection = isStringMap ? null : BeanIntrospector.SHARED.findIntrospection(targetObject.getClass()).orElseThrow();
    }

    private ProxyObjectWithIntrospectableSupport(Context context, Object target, boolean isStringMap, BeanIntrospection<?> introspection) {
        this.context = context;
        this.target = target;
        this.isStringMap = isStringMap;
        this.introspection = introspection;
    }

    private static boolean isStringMap(Object obj) {
        if (obj instanceof Map<?, ?> map) {
            return map.keySet().stream().allMatch(it -> it instanceof String);
        } else {
            return false;
        }
    }

    @Override
    public Object getMember(String key) {
        Map<String, Object> map = asMap();

        // Is it a property?
        Object result = map.get(key);
        if (result != null) {
            boolean resultIsMap = isStringMap(result);
            var resultIntrospection = BeanIntrospector.SHARED.findIntrospection(result.getClass());
            if (resultIsMap || resultIntrospection.isPresent()) {
                return new ProxyObjectWithIntrospectableSupport(context, result, resultIsMap, resultIntrospection.orElseThrow());
            } else {
                return context.asValue(result);
            }
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

        return context.asValue(null);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap() {
        return isStringMap ? (Map<String, Object>) target : BeanMap.of(target);
    }

    @SuppressWarnings("rawtypes")
    private class PolyglotBeanMethod implements ProxyExecutable {
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
}
