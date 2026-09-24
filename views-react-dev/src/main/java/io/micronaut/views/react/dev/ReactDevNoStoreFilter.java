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
package io.micronaut.views.react.dev;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.ResponseFilter;
import io.micronaut.http.annotation.ServerFilter;

/**
 * Stops the browser caching a page it is about to be asked to reload.
 *
 * <p>Without this the refresh is pointless. A rendered page carries no {@code Cache-Control} and no
 * validator, so the browser is free to cache it heuristically and answer its own
 * {@code location.reload()} from that cache -- measured, in a real browser: it reloaded, was served
 * the document it already had, and stopped, because the token it came back with was current.
 *
 * <p>Only the pages this module is refreshing, and only in development.
 */
@Internal
@Requires(bean = ReactDevConfiguration.class)
@ServerFilter(ServerFilter.MATCH_ALL_PATTERN)
final class ReactDevNoStoreFilter {

    /**
     * Marks every response no-store while dev reload is on.
     *
     * @param request  the request being answered
     * @param response the response to mark
     */
    @ResponseFilter
    public void filterResponse(HttpRequest<?> request, MutableHttpResponse<?> response) {
        response.header(HttpHeaders.CACHE_CONTROL, "no-store, must-revalidate");
    }
}
