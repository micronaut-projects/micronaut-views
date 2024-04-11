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

/**
 * HTMX Response Headers.
 * @see <a href="https://htmx.org/reference/#response_headers">Response Headers Reference</a>
 * @author Sergio del Amo
 * @since 5.2.0
 */
public final class HtmxResponseHeaders {
    /**
     * Allows you to do a client-side redirect that does not do a full page reload.
     *
     * @see <a href="https://htmx.org/headers/hx-location/">HX-Location</a>
     */
    public static final String HX_LOCATION = "HX-Location";

    /**
     * Allows you to replace the current URL in the location bar.
     *
     * @see <a href="https://htmx.org/headers/hx-replace-url/">HX-Replace-Url</a>
     */
    public static final String HX_REPLACE_URL = "HX-Replace-Url";

    /**
     * Pushes a new url into the history stack.
     *
     * @see <a href="https://htmx.org/headers/hx-push-url">HX-Push</a>
     */
    public static final String HX_PUSH_URL = "HX-Push-Url";

    /**
     * Can be used to do a client-side redirect to a new location.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Redirect</a>
     */
    public static final String HX_REDIRECT = "HX-Redirect";

    /**
     * Can be used to do a full refresh of the page on the client-side.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Refresh</a>
     */
    public static final String HX_REFRESH = "HX-Refresh";

    /**
     * A CSS selector that updates the target of the content update to a different element on the page.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Retarget</a>
     */
    public static final String HX_RETARGET = "HX-Retarget";

    /**
     * A CSS selector that allows you to choose which part of the response is used to be swapped in.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Reselect</a>
     */
    public static final String HX_RESELECT = "HX-Reselect";

    /**
     * Can be used to trigger client side events.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger</a>
     */
    public static final String HX_TRIGGER = "HX-Trigger";

    /**
     * Can be used to trigger client side events after the <a href="https://htmx.org/docs/#request-operations">settling step</a>.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger-After-Settle</a>
     */
    public static final String HX_TRIGGER_AFTER_SETTLE = "HX-Trigger-After-Settle";

    /**
     * Can be used to trigger client side events after the <a href="https://htmx.org/docs/#request-operations">swap step</a>.
     *
     * @see <a href="https://htmx.org/headers/hx-trigger/">HX-Trigger-After-Settle</a>
     */
    public static final String HX_TRIGGER_AFTER_SWAP = "HX-Trigger-After-Swap";

    /**
     * Allows you to specify how the response will be swapped.
     *
     * @see <a href="https://htmx.org/reference/#response_headers">HX-Reswap</a>
     */
    public static final String HX_RESWAP = "HX-Reswap";

    private HtmxResponseHeaders() {
    }
}
