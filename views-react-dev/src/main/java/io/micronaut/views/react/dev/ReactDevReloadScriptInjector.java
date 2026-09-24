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
import io.micronaut.http.HttpRequest;
import io.micronaut.views.react.ReactRenderPostProcessor;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.Writer;

/**
 * Adds the script that listens for a rebuild to every rendered page.
 *
 * <p>The page carries the bundle token it was rendered with, and reloads only when the server
 * reports a different one. Reacting to the event alone does not work: the browser drops its
 * connection while navigating, so it can come back having been served markup from before the swap
 * and then never hear again. Measured -- a real browser reloaded once, too early, and sat there
 * showing the old page.
 *
 * <p><strong>A render with no request gets nothing.</strong> That is how
 * {@code micronaut-email-template} renders, and a script tag in a delivered message is at best
 * noise -- the same trap that once put the hydration bootstrap into email bodies.
 */
@Internal
@Requires(bean = ReactDevConfiguration.class)
@Singleton
final class ReactDevReloadScriptInjector implements ReactRenderPostProcessor {
    private static final String SCRIPT = """
        <script data-micronaut-views-react-dev-reload>
        (function () {
          var rendered = '%s';
          var source = new EventSource('%s');
          // Exposed so the state of the connection can be inspected from the console, or from a
          // test driving a real browser. This is a development module; there is nothing to hide.
          var state = window.__micronautViewsReactDevReload = {
            rendered: rendered, opens: 0, errors: 0, messages: [], source: source
          };
          source.addEventListener('open', function () { state.opens++; });
          source.addEventListener('error', function () { state.errors++; });
          source.addEventListener('reload', function (event) {
            state.messages.push(event.data);
            if (event.data !== rendered) {
              location.reload();
            }
          });
        })();
        </script>""";

    private final ReactDevReloadBroadcaster broadcaster;
    private final String path;

    ReactDevReloadScriptInjector(ReactDevConfiguration configuration, ReactDevReloadBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
        this.path = configuration.getPath();
    }

    @Override
    public void afterRender(Writer writer, HttpRequest<?> request) throws IOException {
        if (request == null) {
            return;
        }
        writer.write(SCRIPT.formatted(broadcaster.currentToken(), path));
    }
}
