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
package io.micronaut.views.http;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;
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
import io.micronaut.http.context.ServerRequestContext;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.ModelAndViewRenderer;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * A message body handler for {@link ModelAndView}.
 *
 * @param <T> The model type
 * @author Tim Yates
 * @since 6.0.0
 */
@Internal
@Singleton
public final class RawModelAndViewMessageBodyHandler<T> implements TypedMessageBodyWriter<ModelAndView<T>>, ResponseBodyWriter<ModelAndView<T>> {

    private static final Logger LOG = LoggerFactory.getLogger(RawModelAndViewMessageBodyHandler.class);

    private final ModelAndViewRenderer<T, HttpRequest<?>> modelAndViewRenderer;

    public RawModelAndViewMessageBodyHandler(ModelAndViewRenderer<T, HttpRequest<?>> modelAndViewRenderer) {
        this.modelAndViewRenderer = modelAndViewRenderer;
    }

    @Override
    public @NonNull Argument<ModelAndView<T>> getType() {
        return (Argument) Argument.of(ModelAndView.class);
    }

    @Override
    public boolean isBlocking() {
        return true;
    }

    @Override
    public void writeTo(@NonNull Argument<ModelAndView<T>> type, @NonNull MediaType mediaType, ModelAndView<T> object, @NonNull MutableHeaders outgoingHeaders, @NonNull OutputStream outputStream) throws CodecException {
        // The request is required at the moment for the Soy and Pebble renderers; Soy needs to get an Attribute from it, and Pebble needs it to resolve the locale
        try {
            Optional<Writable> optional = modelAndViewRenderer.render(object, ServerRequestContext.currentRequest().orElse(null), mediaType.toString());
            if (optional.isEmpty()) {
                return;
            }
            outgoingHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
            optional.get().writeTo(outputStream);
        } catch (Exception e) {
            throw wrap(e);
        }
    }

    @Override
    public @NonNull ByteBodyHttpResponse<?> write(@NonNull ByteBodyFactory bodyFactory, @NonNull HttpRequest<?> request, @NonNull MutableHttpResponse<ModelAndView<T>> httpResponse, @NonNull Argument<ModelAndView<T>> type, @NonNull MediaType mediaType, ModelAndView<T> object) throws CodecException {
        httpResponse.getHeaders().contentTypeIfMissing(mediaType);
        return ByteBodyHttpResponseWrapper.wrap(httpResponse, writePiece(bodyFactory, request, httpResponse, type, mediaType, object));
    }

    @Override
    public @NonNull CloseableByteBody writePiece(@NonNull ByteBodyFactory bodyFactory, @NonNull HttpRequest<?> request, @NonNull HttpResponse<?> response, @NonNull Argument<ModelAndView<T>> type, @NonNull MediaType mediaType, ModelAndView<T> object) throws CodecException {
        // implementing ResponseBodyWriter allows us to avoid ServerRequestContext
        try {
            Optional<Writable> optional = modelAndViewRenderer.render(object, request, mediaType.toString());
            if (optional.isEmpty()) {
                return bodyFactory.createEmpty();
            }
            return bodyFactory.buffer(os -> optional.get().writeTo(os));
        } catch (Exception e) {
            throw wrap(e);
        }
    }

    public void writePieceTo(OutputStream dst, @NonNull HttpRequest<?> request, @NonNull MediaType mediaType, ModelAndView<T> object) {
        try {
            Optional<Writable> optional = modelAndViewRenderer.render(object, request, mediaType.toString());
            if (optional.isEmpty()) {
                return;
            }
            optional.get().writeTo(dst);
        } catch (Exception e) {
            throw wrap(e);
        }
    }

    private CodecException wrap(Exception e) {
        if (e instanceof CodecException ce) {
            return ce;
        } else if (e instanceof IOException) {
            if (LOG.isErrorEnabled()) {
                LOG.error("IOException writing ModelAndView to OutputStream", e);
            }
            return new CodecException("IOException writing ModelAndView to OutputStream", e);
        } else {
            return new CodecException(e.getMessage(), e);
        }
    }
}
