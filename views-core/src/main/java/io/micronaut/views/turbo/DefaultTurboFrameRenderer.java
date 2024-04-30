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

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewsModelDecorator;
import io.micronaut.views.ViewsRendererLocator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * {@link io.micronaut.context.annotation.DefaultImplementation} of {@link TurboFrameRenderer}.
 * @author Sergio del Amo
 * @since 3.4.0
 */
@Singleton
@Requires(classes = HttpRequest.class)
public class DefaultTurboFrameRenderer extends AbstractTurboRenderer<TurboFrame.Builder> implements TurboFrameRenderer {

    /**
     * Constructor.
     * @param viewsRendererLocator Views Renderer Locator.
     * @param viewsModelDecorator Views Model Decorator
     */
    @Inject
    public DefaultTurboFrameRenderer(
        ViewsRendererLocator viewsRendererLocator,
        ViewsModelDecorator viewsModelDecorator
    ) {
        super(viewsRendererLocator, viewsModelDecorator, "text/html");
    }

    /**
     *
     * @param viewsRendererLocator View Renderer Locator
     * @deprecated Use {@link #DefaultTurboFrameRenderer(ViewsRendererLocator, ViewsModelDecorator)} instead.
     */
    @Deprecated(since = "5.2.1", forRemoval = true)
    public DefaultTurboFrameRenderer(ViewsRendererLocator viewsRendererLocator) {
        this(viewsRendererLocator, null);
    }
}
