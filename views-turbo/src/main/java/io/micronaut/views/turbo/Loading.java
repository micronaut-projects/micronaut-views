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
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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

    private static final Map<String, Loading> LOADING_MAP = Collections.unmodifiableMap(initializeMapping());

    private final String value;

    /**
     *
     * @param value `eager` or `lazy`.
     */
    Loading(String value) {
        this.value = value;
    }

    /**
     *
     * @return `eager` or `lazy`.
     */
    @NonNull
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @NonNull
    @SuppressWarnings("java:S2259") // StringUtils.isEmpty performs a null check
    public static Optional<Loading> of(@Nullable String str) {
        if (StringUtils.isEmpty(str)) {
            return Optional.empty();
        }
        return Optional.ofNullable(LOADING_MAP.get(str.toLowerCase(Locale.ENGLISH)));
    }

    private static Map<String, Loading> initializeMapping() {
        Map<String, Loading> m = new HashMap<>();
        for (Loading v : Loading.values()) {
            m.put(v.toString(), v);
        }
        return m;
    }
}
