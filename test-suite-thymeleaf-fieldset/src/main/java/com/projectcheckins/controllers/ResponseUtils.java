package com.projectcheckins.controllers;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ResponseUtils {
    private ResponseUtils() {

    }
    public static MutableHttpResponse<?> respond(@NonNull HttpRequest<?>request,
                                                 @NonNull Supplier<MutableHttpResponse<?>> html,
                                                 @NonNull Consumer<TurboStream.Builder> turboStream) {
        if (TurboMediaType.acceptsTurboStream(request)) {
            TurboStream.Builder builder = TurboStream.builder();
            String turboFrame = request.getHeaders().get(TurboHttpHeaders.TURBO_FRAME);
            if (turboFrame != null) {
                builder.targetDomId(turboFrame);
            }
            if (turboStream != null) {
                turboStream.accept(builder);
            }
            return HttpResponse.ok(builder);
        }
        return html.get();
    }
}
