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
 * Turbo Visit Action.
 * <a href="https://turbo.hotwired.dev/handbook/drive#application-visits">Turbo Drive Application Visits</a>
 * @author Sergio del Amo
 * @since 3.4.0
 */
public enum VisitAction {
    RESTORE("restore"),
    REPLACE("replace"),
    ADVANCE("advance");

    @NonNull
    private final String action;

    /**
     *
     * @param action Turbo Visit Action
     */
    VisitAction(String action) {
        this.action = action;
    }

    @NonNull
    public static Optional<VisitAction> of(@NonNull String str) {
        if (str.equals(RESTORE.toString())) {
            return Optional.of(RESTORE);
        } else if (str.equals(REPLACE.toString())) {
            return Optional.of(REPLACE);
        } else if (str.equals(ADVANCE.toString())) {
            return Optional.of(ADVANCE);
        }
        return Optional.empty();
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
}
