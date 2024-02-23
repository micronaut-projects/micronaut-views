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
package io.micronaut.views.turbo.http.bodyswapper;

import io.micronaut.core.annotation.*;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.views.http.ResponseBodySwap;
import io.micronaut.views.http.ResponseBodySwapper;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamAction;
import io.micronaut.views.turbo.TurboStreamView;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * @author Sergio del Amo
 * @since 6.0.0.
 */
@Internal
@Singleton
final class TurboStreamResponseBodySwapper implements ResponseBodySwapper<TurboStream.Builder> {
    private static final String MEMBER_ACTION = "action";
    private static final String MEMBER_TARGET_DOM_ID = "targetDomId";
    private static final String MEMBER_TARGET_CSS_QUERY_SELECTOR = "targetCssQuerySelector";
    private static final int ORDER = 10;

    /**
     * Creates a Turbo Stream builder if annotation {@link TurboStreamView} is found in the route and the request accepts turbo stream content type.
     * @param request HTTP Request
     * @return A Turbo Stream builder
     */
    @Override
    @NonNull
    public Optional<ResponseBodySwap<TurboStream.Builder>> swap(@NonNull HttpRequest<?> request, @Nullable HttpResponse<?> response) {
        if (!TurboMediaType.acceptsTurboStream(request)) {
            return Optional.empty();
        }
        if (response == null) {
            return Optional.empty();
        }
        Object body = response.body();
        if (body instanceof TurboStream.Builder) {
            return Optional.empty();
        }
        if (body instanceof TurboStream) {
            return Optional.empty();
        }
        return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                .flatMap(route -> route.findAnnotation(TurboStreamView.class))
                .map(annotationValue -> of(annotationValue, request.getHeaders(), body))
                .map(b -> new ResponseBodySwap<>(b, TurboMediaType.TURBO_STREAM));
    }

    @NonNull
    private static TurboStream.Builder of(@NonNull AnnotationValue<TurboStreamView> turboViewAnnotation, @NonNull HttpHeaders httpHeaders, @Nullable Object body) {
        TurboStream.Builder builder = TurboStream.builder();
        turboViewAnnotation.stringValue().ifPresent(builder::templateView);
        builder.action(turboViewAnnotation.enumValue(MEMBER_ACTION, TurboStreamAction.class).orElse(TurboStreamAction.UPDATE));
        turboViewAnnotation.stringValue(MEMBER_TARGET_DOM_ID).ifPresent(builder::targetDomId);
        turboViewAnnotation.stringValue(MEMBER_TARGET_CSS_QUERY_SELECTOR).ifPresent(builder::targetCssQuerySelector);
        if (builder.getTargetCssQuerySelector().isEmpty() && builder.getTargetDomId().isEmpty()) {
            httpHeaders
                    .get(TurboHttpHeaders.TURBO_FRAME, String.class)
                    .ifPresent(builder::targetDomId);
        }
        if (body != null) {
            builder.templateModel(body);
        }
        return builder;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
