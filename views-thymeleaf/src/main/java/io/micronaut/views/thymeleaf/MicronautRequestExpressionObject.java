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
package io.micronaut.views.thymeleaf;

import io.micronaut.core.annotation.Internal;
import io.micronaut.http.HttpRequest;
import org.jspecify.annotations.Nullable;

import java.net.URI;

/**
 * Micronaut-native request expression object exposed to Thymeleaf as {@code #request}.
 *
 * @author Sergio del Amo
 * @since 6.0.1
 */
@Internal
public final class MicronautRequestExpressionObject {

    private final HttpRequest<?> request;

    @Nullable
    private final String contextPath;

    MicronautRequestExpressionObject(HttpRequest<?> request, @Nullable String contextPath) {
        this.request = request;
        this.contextPath = contextPath;
    }

    /**
     * @return the configured server context path, or an empty string when no context path is configured.
     */
    public String getContextPath() {
        return contextPath == null ? "" : contextPath;
    }

    /**
     * @return the HTTP method name.
     */
    public String getMethod() {
        return request.getMethodName();
    }

    /**
     * @return the Micronaut request path.
     */
    public String getPath() {
        return request.getPath();
    }

    /**
     * @return the request URI path without the query string.
     */
    public String getRequestURI() {
        return request.getUri().getRawPath();
    }

    /**
     * @return the request URL when available, or Micronaut's relative URI path when the server request has no absolute URL.
     */
    public String getRequestURL() {
        URI uri = request.getUri();
        if (!uri.isAbsolute()) {
            return getRequestURI();
        }
        StringBuilder url = new StringBuilder();
        url.append(uri.getScheme()).append("://");
        if (uri.getRawAuthority() != null) {
            url.append(uri.getRawAuthority());
        } else {
            url.append(request.getServerName());
            int port = request.getServerAddress().getPort();
            url.append(':').append(port);
        }
        url.append(uri.getRawPath());
        return url.toString();
    }

    /**
     * @return the raw query string, or {@code null} if the request URI does not contain one.
     */
    @Nullable
    public String getQueryString() {
        return request.getUri().getRawQuery();
    }
}
