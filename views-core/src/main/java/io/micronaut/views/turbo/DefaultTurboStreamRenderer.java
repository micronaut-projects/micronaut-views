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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewsRendererLocator;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link TurboStreamRenderer}.
 * @author Sergio del Amo
 * @since 3.3.0
 */
@Singleton
@Requires(classes = HttpRequest.class)
public class DefaultTurboStreamRenderer implements TurboStreamRenderer {

    protected final ViewsRendererLocator viewsRendererLocator;

    public DefaultTurboStreamRenderer(ViewsRendererLocator viewsRendererLocator) {
        this.viewsRendererLocator = viewsRendererLocator;
    }

    @Override
    @NonNull
    public Optional<Writable> render(@NonNull TurboStream.Builder builder,
                                     @Nullable HttpRequest<?> request) {
        return builder.getTemplateView()
                .map(viewName ->  {
                    Object model =  builder.getTemplateModel().orElse(null);
                    return viewsRendererLocator.resolveViewsRenderer(viewName, TurboMediaType.TURBO_STREAM, model)
                            .flatMap(renderer -> builder.template(renderer.render(viewName, model, request))
                                    .build()
                                    .render());
                })
                .orElseGet(() -> builder.build().render());
    }

    @Override
    public Optional<Writable> render(HttpRequest<?> request, TurboStream.@NonNull Builder... builders) {
        List<Writable> writableList = new ArrayList<>();
        for (TurboStream.Builder builder : builders) {
            render(builder, request).ifPresent(writableList::add);
        }
        return Optional.of(out -> {
            for (Writable w : writableList) {
                w.writeTo(out);
            }
        });
    }
}
