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
 * Turbo Visit Action.
 * <a href="https://turbo.hotwired.dev/handbook/drive#application-visits">Turbo Drive Application Visits</a>
 * @author Sergio del Amo
 * @since 3.4.0
 */
public enum VisitAction {
    RESTORE("restore"),
    REPLACE("replace"),
    ADVANCE("advance");

    private static final Map<String, VisitAction> ACTION_MAP = Collections.unmodifiableMap(initializeMapping());

    @NonNull
    private final String action;

    /**
     *
     * @param action Turbo Visit Action
     */
    VisitAction(String action) {
        this.action = action;
    }

    /**
     *
     * @return Turbo Visit Action in lowercase
     */
    @NonNull
    public String getAction() {
        return action;
    }

    /**
     *
     * @return Turbo Visit Action in lowercase
     */
    @Override
    public String toString() {
        return getAction();
    }

    @NonNull
    public static Optional<VisitAction> of(@Nullable String str) {
        if (StringUtils.isEmpty(str)) {
            return Optional.empty();
        }
        return Optional.ofNullable(ACTION_MAP.get(str.toLowerCase(Locale.ENGLISH)));
    }

    private static Map<String, VisitAction> initializeMapping() {
        Map<String, VisitAction> m = new HashMap<>();
        for (VisitAction v : VisitAction.values()) {
            m.put(v.toString(), v);
        }
        return m;
    }
}
