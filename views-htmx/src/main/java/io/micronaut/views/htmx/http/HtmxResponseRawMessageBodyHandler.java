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
package io.micronaut.views.htmx.http;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.ByteBodyHttpResponse;
import io.micronaut.http.ByteBodyHttpResponseWrapper;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.body.ByteBodyFactory;
import io.micronaut.http.body.CloseableByteBody;
import io.micronaut.http.body.ResponseBodyWriter;
import io.micronaut.http.body.TypedMessageBodyWriter;
import io.micronaut.http.codec.CodecException;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.http.RawModelAndViewMessageBodyHandler;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;

import java.io.OutputStream;

/**
 * {@link io.micronaut.http.body.MessageBodyWriter} implementation for {@link HtmxResponse}.
 * @author Sergio del Amo
 * @since 5.2.0
 * @param <T> The model type
 */
@Internal
@Singleton
final class HtmxResponseRawMessageBodyHandler<T> implements TypedMessageBodyWriter<HtmxResponse<T>>, ResponseBodyWriter<HtmxResponse<T>> {
    private final RawModelAndViewMessageBodyHandler<T> delegate;

    public HtmxResponseRawMessageBodyHandler(RawModelAndViewMessageBodyHandler<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public @NonNull Argument<HtmxResponse<T>> getType() {
        return (Argument) Argument.of(HtmxResponse.class);
    }

    @Override
    public void writeTo(@NonNull Argument<HtmxResponse<T>> type, @NonNull MediaType mediaType, HtmxResponse<T> object, @NonNull MutableHeaders outgoingHeaders, @NonNull OutputStream outputStream) throws CodecException {
        outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        for (ModelAndView<T> modelAndView : object.getModelAndViews()) {
            delegate.writeTo(null, mediaType, modelAndView, outgoingHeaders, outputStream);
        }
    }

    @Override
    public @NonNull ByteBodyHttpResponse<?> write(@NonNull ByteBodyFactory bodyFactory, @NonNull HttpRequest<?> request, @NonNull MutableHttpResponse<HtmxResponse<T>> httpResponse, @NonNull Argument<HtmxResponse<T>> type, @NonNull MediaType mediaType, HtmxResponse<T> object) throws CodecException {
        httpResponse.getHeaders().contentTypeIfMissing(mediaType);
        return ByteBodyHttpResponseWrapper.wrap(httpResponse, writePiece(bodyFactory, request, httpResponse, type, mediaType, object));
    }

    @Override
    public @NonNull CloseableByteBody writePiece(@NonNull ByteBodyFactory bodyFactory, @NonNull HttpRequest<?> request, @NonNull HttpResponse<?> response, @NonNull Argument<HtmxResponse<T>> type, @NonNull MediaType mediaType, HtmxResponse<T> object) throws CodecException {
        return bodyFactory.buffer(os -> {
            for (ModelAndView<T> modelAndView : object.getModelAndViews()) {
                delegate.writePieceTo(os, request, mediaType, modelAndView);
            }
        });
    }
}
