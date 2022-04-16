/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.views.turbo;

import io.micronaut.core.annotation.NonNull;

import java.util.Optional;

/**
 * Behaviour for changes to the src attribute.
 *
 * @author Sergio del Amo
 * @since 3.4.0
 */
public enum Loading {
    /**
     * When loading="eager", changes to the src attribute will immediately navigate the element.
     */
    EAGER("eager"),

    /**
     * When loading="lazy", changes to the src attribute will defer navigation until the element is visible in the viewport.
     */
    LAZY("lazy");

    private final String loading;

    /**
     *
     * @param loading `eager` or `lazy`.
     */
    Loading(String loading) {
        this.loading = loading;
    }

    @NonNull
    public static Optional<Loading> of(@NonNull String str) {
        if (str.equals(LAZY.toString())) {
            return Optional.of(LAZY);
        } else if (str.equals(EAGER.toString())) {
            return Optional.of(EAGER);
        }
        return Optional.empty();
    }

    /**
     *
     * @return `eager` or `lazy`.
     */
    @NonNull
    public String getLoading() {
        return loading;
    }

    @Override
    public String toString() {
        return getLoading();
    }
}
