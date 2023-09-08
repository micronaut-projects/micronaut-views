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
package io.micronaut.views.jstachio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output;
import io.jstach.jstachio.Template;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.body.MessageBodyWriter;
import io.micronaut.http.codec.CodecException;
import jakarta.inject.Singleton;

/**
 * A message body writer for JStachio JStache annotated models.
 * If the {@link JStachio} supports the body type it writes the template.
 * If the HTTP Header Content-Type is not present, it adds it to the response HTTP Headers with the value text/html.
 * @param <T> type to be encoded
 * @author agentgt
 * @since 4.1.0
 */
@Singleton
@Produces({MediaType.TEXT_HTML})
public class JStachioMessageBodyWriter<T> implements MessageBodyWriter<T> {
    private static final String HTML_UTF_8 = MediaType.TEXT_HTML + "; charset=UTF-8";
    private final JStachio jstachio;

    /**
     *
     * @param jstachio JStachio
     */
    public JStachioMessageBodyWriter(JStachio jstachio) {
        this.jstachio = jstachio;
    }

    @Override
    public boolean isWriteable(@NonNull Argument<T> type, @Nullable MediaType mediaType) {
        return jstachio.supportsType(type.getType());
    }

    @Override
    public void writeTo(@NonNull Argument<T> type,
                        @NonNull MediaType mediaType,
                        T object,
                        @NonNull MutableHeaders outgoingHeaders,
                        @NonNull OutputStream outputStream) throws CodecException {
        try {
            var template = jstachio.findTemplate(object);
            populateContentType(outgoingHeaders, template);
            template.write(object, Output.of(outputStream, template.templateCharset()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new CodecException("jstachio failed", e);
        }
    }

    /**
     *  If the HTTP Header Content-Type is not present, it adds it to the response HTTP Headers with the value text/html.
     * @param outgoingHeaders The HTTP headers
     * @param template JStachio Template
     */
    private static void populateContentType(MutableHeaders outgoingHeaders, Template<Object> template) {
        String contentType = outgoingHeaders.get(HttpHeaders.CONTENT_TYPE);
        if (contentType == null || contentType.equals(MediaType.TEXT_HTML)) {
            outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, template.templateCharset().equals(StandardCharsets.UTF_8) ?
                HTML_UTF_8 :
                MediaType.TEXT_HTML);
        }
    }
}
