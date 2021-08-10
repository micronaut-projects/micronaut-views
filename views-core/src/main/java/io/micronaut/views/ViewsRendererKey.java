/*
 * Copyright 2017-2021 original authors
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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

/**
 * Class used as a Map Key to resolve the {@link ViewsRenderer} to be used for a specific view, content type and body class.
 * @author Sergio del Amo
 * @since 3.0.0
 */
@Internal
class ViewsRendererKey {

    @NonNull
    private final String viewName;

    @NonNull
    private final String contentType;

    @Nullable
    private final Class<?> bodyClass;

    /**
     *
     * @param viewName View name
     * @param contentType Response content type
     * @param bodyClass Response body class
     */
    public ViewsRendererKey(@NonNull String viewName, @NonNull String contentType, @Nullable Class<?> bodyClass) {
        this.viewName = viewName;
        this.contentType = contentType;
        this.bodyClass = bodyClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ViewsRendererKey that = (ViewsRendererKey) o;

        if (!viewName.equals(that.viewName)) {
            return false;
        }
        if (!contentType.equals(that.contentType)) {
            return false;
        }
        return bodyClass != null ? bodyClass.equals(that.bodyClass) : that.bodyClass == null;
    }

    @Override
    public int hashCode() {
        int result = viewName.hashCode();
        result = 31 * result + contentType.hashCode();
        result = 31 * result + (bodyClass != null ? bodyClass.hashCode() : 0);
        return result;
    }
}
