/*
 * Copyright 2017-2024 original authors
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
package io.micronaut.views;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;

import java.util.Optional;

/**
 * Abstract class to be used by builders which support templates.
 * @author Sergio del Amo
 * @since 3.4.0
 * @param <T> The class to be built
 */
@SuppressWarnings("java:S119") // SELF is a better name here
public abstract class TemplatedBuilder<T extends Renderable, SELF extends TemplatedBuilder<T, SELF>> {

    @Nullable
    private Object template;

    @Nullable
    private String templateView;

    @Nullable
    private Object templateModel;

    /**
     *
     * @return Build instance
     */
    public abstract T build();

    /**
     *
     * @return The TurboStream template view name.
     */
    @NonNull
    public Optional<String> getTemplateView() {
        return Optional.ofNullable(templateView);
    }

    /**
     *
     * @return The TurboStream template model.
     */
    @NonNull
    public Optional<Object> getTemplateModel() {
        return Optional.ofNullable(templateModel);
    }

    /**
     *
     * @return The Template
     */
    @Nullable
    public Object getTemplate() {
        return template;
    }

    /**
     * Sets the template with a View and Model.
     * @param view The View name
     * @param model The Model
     * @return The Builder
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public SELF template(@NonNull String view, Object model) {
        this.templateView = view;
        this.templateModel = model;
        return (SELF) this;
    }

    /**
     * Sets the Turbo Frame  with a String. E.g. HTML.
     * @param html The turbo frame content
     * @return The Builder
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public SELF template(@NonNull CharSequence html) {
        this.template = html;
        return (SELF) this;
    }

    /**
     * Sets the Turbo frame content with a {@link Writable}.
     * @param writable The template as a {@link Writable}.
     * @return The Builder
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public SELF template(@NonNull Writable writable) {
        this.template = writable;
        return (SELF) this;
    }

    /**
     * Sets the template's view name.
     * @param templateView The View name
     * @return The Builder
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public SELF templateView(@NonNull String templateView) {
        this.templateView = templateView;
        return (SELF) this;
    }

    /**
     * Sets the template's model.
     * @param templateModel template model.
     * @return The Builder
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public SELF templateModel(@NonNull Object templateModel) {
        this.templateModel = templateModel;
        return (SELF) this;
    }
}
