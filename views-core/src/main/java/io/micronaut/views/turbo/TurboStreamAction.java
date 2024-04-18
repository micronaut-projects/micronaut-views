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

/**
 * Turbo Seven Actions.
 * <a href="https://turbo.hotwired.dev/reference/streams#the-seven-actions">Turbo Seven Actions</a>
 * @author Sergio del Amo
 * @since 3.3.0
 */
public enum TurboStreamAction {
    /**
     * Appends the content within the template tag to the container designated by the target dom id.
     */
    APPEND("append"),

    /**
     * Prepends the content within the template tag to the container designated by the target dom id.
     */
    PREPEND("prepend"),

    /**
     * Replaces the element designated by the target dom id.
     */
    REPLACE("replace"),

    /**
     * Updates the content within the template tag to the container designated by the target dom id.
     */
    UPDATE("update"),

    /**
     * Removes the element designated by the target dom id.
     */
    REMOVE("remove"),

    /**
     * Inserts the content within the template tag before the element designated by the target dom id.
     */
    BEFORE("before"),

    /**
     * Inserts the content within the template tag after the element designated by the target dom id.
     */
    AFTER("after"),

    /**
     * Replaces the element designated by the target dom id via morph.
     */
    MORPH("morph"),

    /**
     * Initiates a Page Refresh to render new content with morphing.
     */
    REFRESH("refresh");

    @NonNull
    private final String action;

    /**
     *
     * @param action turob stream action
     */
    TurboStreamAction(String action) {
        this.action = action;
    }

    /**
     *
     * @return Turbo action in lowercase
     */
    @NonNull
    public String getAction() {
        return action;
    }

    /**
     *
     * @return Turbo action in lowercase
     */
    @Override
    public String toString() {
        return getAction();
    }
}
