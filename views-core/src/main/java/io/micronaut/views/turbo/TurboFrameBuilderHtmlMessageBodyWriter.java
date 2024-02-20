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
package io.micronaut.views.turbo;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.body.MessageBodyWriter;
import io.micronaut.http.codec.CodecException;
import io.micronaut.views.exceptions.ViewRenderingException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Tim Yates
 * @since 6.0.0
 */
@Produces(MediaType.TEXT_HTML)
@Singleton
public class TurboFrameBuilderHtmlMessageBodyWriter implements MessageBodyWriter<TurboFrame.Builder> {

    private static final Logger LOG = LoggerFactory.getLogger(TurboFrameBuilderHtmlMessageBodyWriter.class);

    private final TurboFrameRenderer turboFrameRenderer;

    public TurboFrameBuilderHtmlMessageBodyWriter(TurboFrameRenderer turboFrameRenderer) {
        this.turboFrameRenderer = turboFrameRenderer;
    }

    @Override
    public void writeTo(@NonNull Argument<TurboFrame.Builder> type,
                        @NonNull MediaType mediaType,
                        TurboFrame.Builder turboStreamBuilder,
                        @NonNull MutableHeaders outgoingHeaders,
                        @NonNull OutputStream outputStream) throws CodecException {
        turboFrameRenderer.render(turboStreamBuilder, null)
                .ifPresent(writable -> {
                    try {
                        writable.writeTo(outputStream);
                    } catch (IOException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error("IOException writing TurboFrame Writeable to OutputStream", e);
                        }
                        throw new ViewRenderingException("IOException writing TurboFrame Writeable to OutputStream", e);
                    }
                });
    }
}
