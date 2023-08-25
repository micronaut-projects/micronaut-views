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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.body.MessageBodyWriter;
import io.micronaut.http.codec.CodecException;
import jakarta.inject.Singleton;

/**
 * A message body writer for JStachio JStache annotated models.
 * To use place <code>&#64;Produces({MediaType.TEXT_HTML})</code>
 * on a controller method and return a JStache annotated model.
 * @param <T> type to be encoded
 * @author agentgt
 * @since 4.1.0
 */
@Singleton
@Produces({MediaType.TEXT_HTML})
public class JStachioMessageBodyWriter<T> implements MessageBodyWriter<T> {

    private final JStachio jstachio;
    
    public JStachioMessageBodyWriter(JStachio jstachio) {
        super();
        this.jstachio = jstachio;
    }

    @Override
    public boolean isWriteable(@NonNull Argument<T> type, @Nullable MediaType mediaType) {
        return jstachio.supportsType(type.getType());
    }
    
    @Override
    public void writeTo(@NonNull Argument<T> type, @NonNull MediaType mediaType, T object,
            @NonNull MutableHeaders outgoingHeaders, @NonNull OutputStream outputStream) throws CodecException {
        try {
            var template = jstachio.findTemplate(object);
            String suffix = "";
            if (template.templateCharset().equals(StandardCharsets.UTF_8)) {
                suffix = "; charset=UTF-8";
            }
            outgoingHeaders.set("Content-Type", MediaType.TEXT_HTML + suffix);
            template.write(object, Output.of(outputStream, template.templateCharset()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new CodecException("jstachio failed", e);
        }
    }

}
