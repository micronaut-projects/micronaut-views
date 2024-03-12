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
package io.micronaut.views.htmx.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;

import java.util.Optional;

/**
 * HTMX Request Headers.
 * @see <a href="https://htmx.org/reference/#request_headers">HTMX Request Headers</a>
 * @author Sergio del Amo
 * @since 6.0.0
 */
public interface HtmxRequestHeaders {
    /**
     * Indicates that the request comes from an element that uses hx-boost.
     */
    String HX_BOOSTED = "HX-Boosted";

    /**
     * The current URL of the browser.
     */
    String HX_CURRENT_URL = "HX-Current-URL";

    /**
     * Indicates if the request is for history restoration after a miss in the local history cache.
     */
    String HX_HISTORY_RESTORE_REQUEST = "HX-History-Restore-Request";

    /**
     * The user response to a <a href="https://htmx.org/attributes/hx-prompt/">hx-prompt</a>.
     */
    String HX_PROMPT = "HX-Prompt";

    /**
     * Only present and {@code true} if the request is issued by htmx.
     */
    String HX_REQUEST = "HX-Request";

    /**
     * The {@code id} of the target element if it exists.
     */
    String HX_TARGET = "HX-Target";

    /**
     * The {@code name} of the triggered element if it exists.
     *
     */
    String HX_TRIGGER_NAME = "HX-Trigger-Name";

    /**
     * The {@code id} of the triggered element if it exists.
     *
     */
    String HX_TRIGGER = "HX-Trigger";

    /**
     *
     * @return Indicates that the request is via an element using hx-boost
     */
    boolean isBoost();

    /**
     *
     * @return the current URL of the browser
     */
    @Nullable
    String getCurrentUrl();

    /**
     *
     * @return “true” if the request is for history restoration after a miss in the local history cache
     */
    @Nullable
    String getHistoryRestoreRequest();

    /**
     *
     * @return the user response to an hx-prompt
     */
    @Nullable
    String getPrompt();

    /**
     *
     * @return the id of the target element if it exists
     */
    @Nullable
    String getTarget();

    /**
     *
     * @return the name of the triggered element if it exists
     */
    @Nullable
    String getTriggerName();

    /**
     *
     * @return the id of the triggered element if it exists
     */
    @Nullable
    String getTrigger();

    /**
     *
     * @param headers HTTP Headers
     * @return an Optional of HtmxRequestHeaders
     */
    static Optional<HtmxRequestHeaders> of(HttpHeaders headers) {
        if (headers.get(HtmxRequestHeaders.HX_REQUEST) == null) {
            return Optional.empty();
        }
        return Optional.of(new HtmxRequestHeaders() {
            @Override
            public boolean isBoost() {
                return headers.get(HX_BOOSTED, Boolean.class).orElse(false);
            }

            @Override
            public String getCurrentUrl() {
                return headers.get(HX_CURRENT_URL);
            }

            @Override
            public String getHistoryRestoreRequest() {
                return headers.get(HX_HISTORY_RESTORE_REQUEST);
            }

            @Override
            public String getPrompt() {
                return headers.get(HX_PROMPT);
            }

            @Override
            public String getTarget() {
                return headers.get(HX_TARGET);
            }

            @Override
            public String getTriggerName() {
                return headers.get(HX_TRIGGER_NAME);
            }

            @Override
            public String getTrigger() {
                return headers.get(HX_TRIGGER);
            }
        });
    }

    /**
     *
     * @param request HTTP Request
     * @return an Optional of HtmxRequestHeaders
     */
    @NonNull
    static Optional<HtmxRequestHeaders> of(HttpRequest<?> request) {
        return of(request.getHeaders());
    }
}
