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
package io.micronaut.views.velocity;

import io.micronaut.context.annotation.Factory;
import org.apache.velocity.app.VelocityEngine;

import jakarta.inject.Singleton;
import java.util.Properties;

/**
 * Factory for the velocity engine.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
@Factory
public class VelocityFactory {

    /**
     * @return The velocity engine
     */
    @Singleton
    public VelocityEngine getVelocityEngine() {
        final Properties p = new Properties();
        p.setProperty("resource.loaders", "class");
        p.setProperty("resource.loader.class.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityEngine(p);
    }
}
