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
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.bind.binders.TypedRequestArgumentBinder;
import jakarta.inject.Singleton;

/**
 * {@link TypedRequestArgumentBinder} for {@link HtmxRequestHeaders}.
 * @author Sergio del Amo
 * @since 5.2.0
 */
@Singleton
@Internal
final class HtmxRequestHeadersTypedRequestArgumentBinder implements TypedRequestArgumentBinder<HtmxRequestHeaders> {
    @Override
    public Argument<HtmxRequestHeaders> argumentType() {
        return Argument.of(HtmxRequestHeaders.class);
    }

    @Override
    public BindingResult<HtmxRequestHeaders> bind(ArgumentConversionContext<HtmxRequestHeaders> context, HttpRequest<?> request) {
        if (!HtmxRequestUtils.isHtmxRequest(request)) {
            return BindingResult.UNSATISFIED;
        }
        return () -> HtmxRequestHeaders.of(request);
    }
}
