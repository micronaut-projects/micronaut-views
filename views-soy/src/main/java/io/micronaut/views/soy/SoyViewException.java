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
package io.micronaut.views.soy;


import io.micronaut.views.exceptions.ViewRenderingException;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import java.util.concurrent.Future;


/**
 * Exception type that wraps errors encountered while rendering a Soy template.
 *
 * @author Sam Gammon (sam@momentum.io)
 * @since 1.3.2
 */
public class SoyViewException extends ViewRenderingException {
    /** Future value that caused this failure, if applicable. */
    private @Nullable final Future future;

    /**
     * Soy render exceptions always wrap other exceptions.
     *
     * @param cause Inner cause of the error encountered while rendering.
     */
    SoyViewException(final @NonNull Throwable cause) {
        super(cause.getMessage(), cause);
        this.future = null;
    }

    /**
     * In some cases, an async operation may cause a failure. In that case, we
     * include the future value for debugging.
     *
     * @param cause Inner cause of the error encountered while rendering.
     * @param future Future value which failed.
     */
    SoyViewException(final @NonNull Throwable cause,
                     final @Nullable Future future) {
        super(cause.getMessage(), cause);
        this.future = future;
    }

    // -- Getters -- //
    /**
     * @return Future value that caused this error, if applicable.
     */
    public @Nullable Future getFuture() {
        return future;
    }
}
