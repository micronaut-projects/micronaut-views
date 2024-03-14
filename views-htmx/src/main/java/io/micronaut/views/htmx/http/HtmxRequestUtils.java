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
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;

/**
 * HTMX Request Utils.
 * @author Sergio del Amo
 * @since 6.0.0
 */
public final class HtmxRequestUtils {
    private HtmxRequestUtils() {
    }

    /**
     *
     * @param request HTTP Request
     * @return Whether the HTTP Request is an HTMX Request. That it is to say it contains the HX-Request HTTP header.
     */
    public static boolean isHtmxRequest(@NonNull HttpRequest<?> request) {
        return request.getHeaders().get(HtmxRequestHeaders.HX_REQUEST) != null;
    }
}
