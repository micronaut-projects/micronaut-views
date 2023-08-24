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

import io.jstach.jstachio.JStachio;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Renders JStachio JStache annotated models.
 *
 * The view name is expected to be {@value JStachioModelAndView#JSTACHIO_VIEW}.
 *
 * @param <T> model data type
 * @param <R> request type
 * @author agentgt
 * @since 4.1.0
 */
@Singleton
public class JStachioViewsRenderer<T, R> implements ViewsRenderer<T, R> {

    private final JStachio jstachio;

    /**
     * Will be called by Micronaut.
     * @param jstachio to be used for rendering
     */
    @Inject
    public JStachioViewsRenderer(JStachio jstachio) {
        super();
        this.jstachio = jstachio;
    }

    @Override
    public @NonNull Writable render(@NonNull String viewName, @Nullable T data, @Nullable R request) {
        return new JStachioWritable(jstachio, JStachioModelAndView.requireModel(data));
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return viewName.equals(JStachioModelAndView.JSTACHIO_VIEW);
    }

}
