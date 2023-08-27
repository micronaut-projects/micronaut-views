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

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.views.DefaultViewsResolver;
import io.micronaut.views.ViewsResolver;
import jakarta.inject.Singleton;

/**
 * Resolves view with JStachio directly.
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Requires(classes = HttpRequest.class)
@Replaces(DefaultViewsResolver.class)
@Singleton
//TODO the name is wrong here but I have left it for diff JStachio not Jstachio
public class JstachioViewResolver implements ViewsResolver { 
    
    private final JStachioViewNameRegistry registry;

    /**
     *
     * @param registry registry
     */
    public JstachioViewResolver(JStachioViewNameRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Optional<String> resolveView(HttpRequest<?> request, HttpResponse<?> response) {
        Object body = response.body();
        return body == null ?
            Optional.empty() :
            resolveView(body.getClass());
    }

    private Optional<String> resolveView(Class<?> bodyType) {
        return registry.resolveView(bodyType);
    }

 
}
