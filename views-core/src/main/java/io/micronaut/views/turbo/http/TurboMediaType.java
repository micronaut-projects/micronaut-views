/*
 * Copyright 2017-2022 original authors
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
package io.micronaut.views.turbo.http;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;

/**
 * Utility class to decorate an HTTP response with Turbo content-type.
 * @author Sergio del Amo
 * @since 3.3.0
 */
public final class TurboMediaType {
    /**
     * Turbo Stream Content Type.
     */
    public static final String TURBO_STREAM = "text/vnd.turbo-stream.html";

    /**
     * Turbo Stream Content Type.
     */
    public static final MediaType TURBO_STREAM_TYPE = new MediaType(TURBO_STREAM);

    private TurboMediaType() {
    }

    public static boolean acceptsTurboStream(@NonNull HttpRequest<?> request) {
        return request.getHeaders()
                .accept()
                .stream()
                .anyMatch(mediaType -> mediaType.toString().equals(TurboMediaType.TURBO_STREAM));
    }
}
