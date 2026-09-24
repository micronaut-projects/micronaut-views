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
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.ResponseFilter;
import io.micronaut.http.annotation.ServerFilter;

/**
 * Adds the script that listens for a rebuild to rendered HTML.
 *
 * <p>Done here, rather than from the renderer or the render script, so that
 * {@code micronaut-views-react} needs no hook and a custom
 * {@code micronaut.views.react.render-script} keeps working untouched.
 *
 * <p>Filtering HTTP responses also settles the question of what must never receive this. An email
 * body rendered by {@code micronaut-email-template} is not an HTTP response, so it cannot pick the
 * script up by accident -- which is how the hydration bootstrap once ended up in delivered mail.
 */
@Internal
@Requires(bean = ReactDevConfiguration.class)
@ServerFilter(ServerFilter.MATCH_ALL_PATTERN)
final class ReactDevReloadScriptFilter {
    private final String script;

    ReactDevReloadScriptFilter(ReactDevConfiguration configuration) {
        this.script = """
            <script data-micronaut-views-react-dev-reload>
            (function () {
              var source = new EventSource('%s');
              source.addEventListener('reload', function () { location.reload(); });
            })();
            </script>""".formatted(configuration.getPath());
    }

    /**
     * Appends the script to an HTML response.
     *
     * @param request  the request being answered
     * @param response the response to add the script to
     */
    @ResponseFilter
    public void filterResponse(HttpRequest<?> request, MutableHttpResponse<?> response) {
        if (!(response.body() instanceof Writable rendered) || !isHtml(request, response)) {
            return;
        }
        response.body((Writable) writer -> {
            rendered.writeTo(writer);
            writer.write(script);
        });
    }

    /**
     * A view-rendered route has no content type on the response yet: it is applied after response
     * filters run. So read it when it is there, and otherwise go by what the client asked for --
     * which is the question being asked anyway, since only a browser needs to be refreshed.
     */
    private static boolean isHtml(HttpRequest<?> request, MutableHttpResponse<?> response) {
        return response.getContentType()
            .map(ReactDevReloadScriptFilter::isHtml)
            .orElseGet(() -> request.accept().stream().anyMatch(ReactDevReloadScriptFilter::isHtml));
    }

    private static boolean isHtml(MediaType type) {
        return type.matches(MediaType.TEXT_HTML_TYPE) || type.matches(MediaType.APPLICATION_XHTML_TYPE);
    }
}
