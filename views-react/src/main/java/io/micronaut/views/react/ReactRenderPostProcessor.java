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
package io.micronaut.views.react;

import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;

/**
 * Appends to a server-rendered React page once the component has been written.
 *
 * <p>Implement this to add something to every rendered page without touching the render script --
 * a development-mode reload listener is the motivating case, and
 * {@code micronaut-views-react-dev} ships one.
 *
 * <p>There is deliberately no HTTP filter equivalent. A {@code @View} route's response body is a
 * {@link io.micronaut.views.ModelAndView} while filters run, and the markup is not produced until a
 * {@code MessageBodyWriter} encodes it, after the last filter. This is the point at which rendered
 * markup exists.
 *
 * <p>Beans are invoked in {@link Ordered} order, after the component's own output and before the
 * writer is flushed.
 *
 * @since 6.2.1
 */
public interface ReactRenderPostProcessor extends Ordered {
    /**
     * Called once per render, after the component has written its markup.
     *
     * @param writer  the writer the page was rendered to
     * @param request the request being answered, or {@code null} for a render that is not going to
     *                a browser -- an email body built by {@code micronaut-email-template} is the
     *                common case, and usually wants nothing added
     * @throws IOException if writing fails
     */
    void afterRender(Writer writer, @Nullable HttpRequest<?> request) throws IOException;
}
