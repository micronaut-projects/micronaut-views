/*
 * Copyright 2017-2026 original authors
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
package io.micronaut.views.jinjava;

import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.el.JinjavaInterpreterResolver;
import com.hubspot.jinjava.el.TypeConvertingMapELResolver;
import com.hubspot.jinjava.el.ext.JinjavaBeanELResolver;
import com.hubspot.jinjava.el.ext.JinjavaListELResolver;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;
import jinjava.javax.el.ArrayELResolver;
import jinjava.javax.el.CompositeELResolver;
import jinjava.javax.el.ELResolver;
import jinjava.javax.el.ResourceBundleELResolver;

/**
 * Gives the Jinjava engine of a context in development mode an EL resolver of its own, in development mode only. Jinjava's
 * default resolvers are static, and their {@code BeanELResolver} caches every class a template reads, strongly and for
 * good: one held by the parent tier would keep every generation that rendered a template. A copy of the default resolver
 * goes with its context. A resolver the application set itself is left as it is.
 *
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
final class JinjavaDevelopmentElResolver implements BeanCreatedEventListener<JinjavaViewsRendererConfigurationProperties> {

    @Override
    public JinjavaViewsRendererConfigurationProperties onCreated(BeanCreatedEvent<JinjavaViewsRendererConfigurationProperties> event) {
        JinjavaConfig.Builder config = event.getBean().getConfig();
        // the builder has no getter: what it would build tells which resolver it holds
        ELResolver configured = config.build().getElResolver();
        if (configured == JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_ONLY) {
            config.withElResolver(copyOfDefault(true));
        } else if (configured == JinjavaInterpreterResolver.DEFAULT_RESOLVER_READ_WRITE) {
            config.withElResolver(copyOfDefault(false));
        }
        return event.getBean();
    }

    /**
     * @param readOnly Whether the resolver is read only
     * @return What {@link JinjavaInterpreterResolver} builds its default resolvers from
     */
    private static ELResolver copyOfDefault(boolean readOnly) {
        CompositeELResolver resolver = new CompositeELResolver();
        resolver.add(new ArrayELResolver(readOnly));
        resolver.add(new JinjavaListELResolver(readOnly));
        resolver.add(new TypeConvertingMapELResolver(readOnly));
        resolver.add(new ResourceBundleELResolver());
        resolver.add(new JinjavaBeanELResolver(readOnly));
        return resolver;
    }
}
