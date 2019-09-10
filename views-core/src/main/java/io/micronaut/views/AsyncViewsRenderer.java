/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.netty.buffer.ByteBuf;
import io.reactivex.Flowable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Asynchronous rendering interface for views in Micronaut. This interface works with reactive types to allow the event
 * loop to take over when the renderer is paused, in cases where renderers support such signals.
 *
 * @see ViewsRenderer for the synchronous version
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface AsyncViewsRenderer extends BaseViewsRenderer {
  /**
   * @param viewName view name to be render
   * @param data     response body to render it with a view
   * @param request  HTTP request
   * @param response HTTP response object assembled so far.
   * @return A writable where the view will be written to.
   */
  @Nonnull
  Flowable<MutableHttpResponse<ByteBuf>> render(
    @Nonnull String viewName,
    @Nullable Object data,
    @Nonnull HttpRequest<?> request,
    @Nonnull MutableHttpResponse<?> response);
}
