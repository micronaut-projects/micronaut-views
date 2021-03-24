/*
 * Copyright 2017-2020 original authors
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
import io.micronaut.core.beans.BeanMap;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.core.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface to be implemented by View Engines implementations.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
public interface ViewsRenderer {

    /**
     * The extension separator.
     */
    String EXTENSION_SEPARATOR = ".";

    /**
     * @param viewName view name to be render
     * @param data     response body to render it with a view
     * @return A writable where the view will be written to.
     */
    @NonNull Writable render(@NonNull String viewName, @Nullable Object data);

    /**
     * @param viewName view name to be render
     * @param data     response body to render it with a view
     * @param request  HTTP request
     * @return A writable where the view will be written to.
     */
    default @NonNull Writable render(@NonNull String viewName, @Nullable Object data,
            @NonNull HttpRequest<?> request) {
        return render(viewName, data);
    }

    /**
     * @param viewName view name to be render
     * @return true if a template can be found for the supplied view name.
     */
    boolean exists(@NonNull String viewName);

    /**
     * Creates a view model for the given data.
     * @param data The data
     * @return The model
     */
    default @NonNull Map<String, Object> modelOf(@Nullable Object data) {
        if (data == null) {
            return new HashMap<>(0);
        }
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return BeanMap.of(data);
    }

}
