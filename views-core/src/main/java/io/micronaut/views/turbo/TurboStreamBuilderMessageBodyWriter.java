/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.turbo;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.body.MessageBodyWriter;
import io.micronaut.http.codec.CodecException;
import io.micronaut.views.exceptions.ViewRenderingException;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Sergio del Amo
 * @since 4.1.0
 */
@Produces(TurboMediaType.TURBO_STREAM)
@Singleton
public class TurboStreamBuilderMessageBodyWriter implements MessageBodyWriter<TurboStream.Builder> {
    private static final Logger LOG = LoggerFactory.getLogger(TurboStreamBuilderMessageBodyWriter.class);

    private final TurboStreamRenderer turboStreamRenderer;

    public TurboStreamBuilderMessageBodyWriter(TurboStreamRenderer turboStreamRenderer) {
        this.turboStreamRenderer = turboStreamRenderer;
    }

    @Override
    public void writeTo(@NonNull Argument<TurboStream.Builder> type,
                        @NonNull MediaType mediaType,
                        TurboStream.Builder turboStreamBuilder,
                        @NonNull MutableHeaders outgoingHeaders,
                        @NonNull OutputStream outputStream) throws CodecException {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, TurboMediaType.TURBO_STREAM);
        turboStreamRenderer.render(turboStreamBuilder, null)
                .ifPresent(writable -> {
                    try {
                        writable.writeTo(outputStream);
                    } catch (IOException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error("IOException writing TurboStream Writeable to OutputStream", e);
                        }
                        throw new ViewRenderingException("IOException writing TurboStream Writeable to OutputStream", e);
                    }
                });

    }
}
