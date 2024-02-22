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
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.*;
import io.micronaut.views.http.ResponseBodySwap;
import io.micronaut.views.http.ResponseBodySwapper;
import io.micronaut.views.turbo.Loading;
import io.micronaut.views.turbo.TurboFrame;
import io.micronaut.views.turbo.TurboFrameView;
import io.micronaut.views.turbo.VisitAction;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import jakarta.inject.Singleton;

import java.util.Optional;

/**
 * Attempts to swap the response body with a {@link TurboFrame.Builder}..
 * @author Sergio del Amo
 * @since 6.0.0
 */
@Internal
@Singleton
class TurboFrameResponseBodySwapper implements ResponseBodySwapper<TurboFrame.Builder> {

    private static final String MEMBER_ACTION = "action";
    private static final String MEMBER_TARGET = "target";
    private static final String MEMBER_ID = "id";
    private static final String MEMBER_SRC = "src";
    private static final String MEMBER_LOADING = "loading";
    private static final String MEMBER_BUSY = "busy";
    private static final String MEMBER_DISABLED = "disabled";
    private static final String MEMBER_AUTOSCROLL = "autoscroll";
    private static final int ORDER = 20;
    private static final String TURBO_FRAME_MEDIA_TYPE = MediaType.TEXT_HTML;

    @Override
    @NonNull
    public Optional<ResponseBodySwap<TurboFrame.Builder>> swap(@NonNull HttpRequest<?> request, @Nullable HttpResponse<?> response) {
        if (response == null) {
            return Optional.empty();
        }
        Object body = response.body();
        Optional<ResponseBodySwap<TurboFrame.Builder>> responseBodySwapOptional = response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                .flatMap(route -> route.findAnnotation(TurboFrameView.class))
                .flatMap(ann -> resolveId(ann, request.getHeaders())
                        .map(id -> turboFrameBuilderOf(ann, id, body)))
                .map(b -> new ResponseBodySwap<>(b, TURBO_FRAME_MEDIA_TYPE));
        if (responseBodySwapOptional.isPresent()) {
            return responseBodySwapOptional;
        }
        return turboFrameBuilderInResponse(body)
                .map(b -> new ResponseBodySwap<>(b, TURBO_FRAME_MEDIA_TYPE));
    }

    @NonNull
    private static Optional<TurboFrame.Builder> turboFrameBuilderInResponse(@Nullable Object body) {
        if (body instanceof TurboFrame.Builder turboBody) {
            return  Optional.of(turboBody);
        } else if (body instanceof TurboFrame turboFrame) {
            return  Optional.of(turboFrame.toBuilder());
        }
        return Optional.empty();
    }

    @NonNull
    private static TurboFrame.Builder instantiateTurboFrameBuilder(@Nullable Object body) {
        return turboFrameBuilderInResponse(body).orElseGet(TurboFrame::builder);
    }

    @NonNull
    private static TurboFrame.Builder turboFrameBuilderOf(@NonNull AnnotationValue<TurboFrameView> ann,
                                                          String id,
                                                          @Nullable Object body) {
        TurboFrame.Builder builder = instantiateTurboFrameBuilder(body);
        builder.id(id);
        ann.stringValue().ifPresent(builder::templateView);
        ann.stringValue(MEMBER_ACTION)
                .flatMap(VisitAction::of)
                .ifPresent(builder::visitAction);
        ann.stringValue(MEMBER_TARGET).ifPresent(builder::target);
        ann.stringValue(MEMBER_SRC).ifPresent(builder::src);
        ann.stringValue(MEMBER_LOADING)
                .flatMap(Loading::of)
                .ifPresent(builder::loading);
        parseBoolean(ann, MEMBER_BUSY).ifPresent(builder::busy);
        parseBoolean(ann, MEMBER_DISABLED).ifPresent(builder::disabled);
        parseBoolean(ann, MEMBER_AUTOSCROLL).ifPresent(builder::autoscroll);
        if (body != null && !(body instanceof TurboFrame.Builder)) {
            builder.templateModel(body);
        }
        return builder;
    }

    private static Optional<String> resolveId(@NonNull AnnotationValue<TurboFrameView> ann,
                                              @NonNull HttpHeaders httpHeaders) {
        Optional<String> idOptional = ann.stringValue(MEMBER_ID);
        if (idOptional.isPresent()) {
            return idOptional;
        }
        return httpHeaders.get(TurboHttpHeaders.TURBO_FRAME, String.class);
    }

    @NonNull
    private static Optional<Boolean> parseBoolean(@NonNull AnnotationValue<TurboFrameView> ann,
                                                  @NonNull String member) {
        return ann.stringValue(member)
                .flatMap(TurboFrameResponseBodySwapper::stringAsBoolean);
    }

    @NonNull
    private static Optional<Boolean> stringAsBoolean(@NonNull String str) {
        if (str.equals(StringUtils.TRUE)) {
            return Optional.of(Boolean.TRUE);
        } else if (str.equals(StringUtils.FALSE)) {
            return Optional.of(Boolean.FALSE);
        }
        return Optional.empty();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
