/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.jstachio;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.jstach.jstachio.JStachio;
import jakarta.inject.Singleton;

/**
 * asdfasdfasdf.
 */
@Singleton
final class JStachioViewNameRegistry {

    private final JStachio jstachio;
    private final ConcurrentHashMap<String, Class<?>> classForView = new ConcurrentHashMap<>();
        
    /**
     * asdfasdf.
     * 
     * @param jstachio
     *            jstachio.
     */
    public JStachioViewNameRegistry(JStachio jstachio) {
        super();
        this.jstachio = jstachio;
    }

    boolean supportsViewName(String viewName) {
        return classForView.containsKey(viewName);
    }

    Optional<String> resolveView(Class<?> bodyType) {
        if (jstachio.supportsType(bodyType)) {
            String name = bodyType.getCanonicalName();
            classForView.put(name, bodyType);
            return Optional.of(name);
        }
        return Optional.empty();
    }

}
