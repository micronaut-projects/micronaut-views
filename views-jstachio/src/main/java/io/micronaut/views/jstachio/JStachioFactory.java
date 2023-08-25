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

import java.util.ServiceLoader;

import io.jstach.jstachio.JStachio;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

/**
 * A factory for JStachio.
 * JStachio does not have much dynamic configuration because it is driven by
 * compile time annotations.
 * @author agentgt
 * @since 4.1.0
 */
@Factory
public class JStachioFactory {
    /**
     * Provides a jstachio instance.
     * @return by default the JStachio static singleton which will use the {@link ServiceLoader} for
     * discovery of extensions and templates.
     */
    @Singleton
    public JStachio jstachio() {
        return JStachio.of();
    }
}
