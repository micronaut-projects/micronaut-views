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
package io.micronaut.views.soy;


import io.micronaut.http.MutableHttpResponse;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;


/**
 * Interface by which Soy render context can be managed and orchestrated by a custom {@link SoyContext} object. Provides
 * the ability to specify variables for <pre>@param</pre> and <pre>@inject</pre> Soy declarations, and the ability to
 * override objects like the {@link SoyNamingMapProvider}.
 *
 * @author Sam Gammon (sam@momentum.io)
 * @see SoyContext Default implementation of this interface
 * @since 1.3.2
 */
public interface SoyContextMediator {
  /**
   * Retrieve properties which should be made available via regular, declared `@param` statements.
   *
   * @return Map of regular template properties.
   */
  @Nonnull Map<String, Object> getProperties();

  /**
   * Retrieve properties and values that should be made available via `@inject`.
   *
   * @param framework Properties auto-injected by the framework.
   * @return Map of injected properties and their values.
   */
  @Nonnull Map<String, Object> getInjectedProperties(Map<String, Object> framework);

  /**
   * Specify a Soy renaming map which overrides the globally-installed map, if any. Renaming must still be activated via
   * config, or manually, for the return value of this method to have any effect.
   *
   * @return {@link SoyNamingMapProvider} that should be used for this render routine.
   */
  default @Nonnull Optional<SoyNamingMapProvider> overrideNamingMap() {
    return Optional.empty();
  }

  /**
   * Finalize an HTTP response rendered by the Micronaut Soy layer. This may include adding any final headers, or
   * adjusting headers, before the response is sent.
   *
   * @param response HTTP response to finalize.
   * @param body Rendered HTTP response body.
   * @param <T> Body object type.
   * @return Response, but finalized.
   */
  default @Nonnull <T> MutableHttpResponse<T> finalizeResponse(@Nonnull MutableHttpResponse<T> response,
                                                               @Nonnull T body) {
    return response.body(body);
  }
}
