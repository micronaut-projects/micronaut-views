/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.micronaut.views;

import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;



/**
 * Interface to be implemented by View Engines implementations.
 *
 * @author Sergio del Amo
 * @since 1.0
 */
public interface ViewsRenderer extends BaseViewsRenderer {
    /**
     * @param viewName view name to be render
     * @param data     response body to render it with a view
     * @return A writable where the view will be written to.
     */
    @Nonnull Writable render(@Nonnull String viewName, @Nullable Object data);

    /**
     * @param viewName view name to be render
     * @param data     response body to render it with a view
     * @param request  HTTP request
     * @return A writable where the view will be written to.
     */
    default @Nonnull Writable render(@Nonnull String viewName, @Nullable Object data,
            @Nonnull HttpRequest<?> request) {
        return render(viewName, data);
    }
}
