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

import java.util.Objects;
import java.util.Optional;

import io.jstach.jstache.JStache;
import io.jstach.jstachio.TemplateModel;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.ModelAndView;

/**
 * Creates a ModelAndView that will use JStachio for rendering.
 * @param <T> JStache annotated model type
 */
public class JStachioModelAndView<T> extends ModelAndView<T> {

    /**
     * Special JStachio view name that will make sure JStachio
     * is used for rendering.
     */
    public static final String JSTACHIO_VIEW_NAME = "jstachio";
    private static final Optional<String> OPTIONAL_JSTACHIO_VIEW = Optional.of(JSTACHIO_VIEW_NAME);

    /**
     * Creates a JStachio ModelAndView, prefer {@link #of(Object)} when possible.
     * @param model an instance of a model annotated with {@link JStache} or a {@link TemplateModel}.
     */
    protected JStachioModelAndView(@NonNull T model) {
        super(JSTACHIO_VIEW_NAME, requireModel(model));
    }
    
    /**
     * Convenience method to create a ModelAndView from a {@link JStache} annotated model.
     * @param <T> model type annotated with JStache
     * @param model an instance of a model annotated with JStache or a {@link TemplateModel}.
     * @return model and view that the JStachio will know to render
     */
    public static <T> @NonNull ModelAndView<T> of(@NonNull T model) {
        return new JStachioModelAndView<T>(model);
    }
    
    static <M> @NonNull M requireModel(@Nullable M model) {
        return Objects.requireNonNull(model, "jstachio requires a NonNull model");
    }

    /**
     * Not supported in this implementation.
     * {@inheritDoc}
     */
    @Override
    public final void setView(String view) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public final Optional<String> getView() {
        return OPTIONAL_JSTACHIO_VIEW;
    }
    
}
