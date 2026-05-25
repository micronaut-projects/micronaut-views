/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.thymeleaf.webexpression;

import io.micronaut.core.annotation.Internal;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.util.HttpHostResolver;
import io.micronaut.views.thymeleaf.WebEngineContext;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.Set;

/**
 * Exposes Micronaut web expression objects to Thymeleaf templates.
 *
 * @author Sergio del Amo
 * @since 6.1.0
 */
@Internal
final class RequestExpressionObjectFactory implements IExpressionObjectFactory {

    private static final String REQUEST_EXPRESSION_OBJECT_NAME = "request";
    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES =
        Collections.singleton(REQUEST_EXPRESSION_OBJECT_NAME);

    private final HttpHostResolver httpHostResolver;
    private final HttpServerConfiguration httpServerConfiguration;

    RequestExpressionObjectFactory(HttpHostResolver httpHostResolver,
                                   HttpServerConfiguration httpServerConfiguration) {
        this.httpHostResolver = httpHostResolver;
        this.httpServerConfiguration = httpServerConfiguration;
    }

    @Override
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (!REQUEST_EXPRESSION_OBJECT_NAME.equals(expressionObjectName) || !(context instanceof WebEngineContext)) {
            return null;
        }
        HttpRequest<?> request = ((WebEngineContext) context).getRequest();
        if (request == null) {
            return null;
        }
        return new RequestExpressionObject(request, httpHostResolver::resolve, httpServerConfiguration.getContextPath());
    }

    @Override
    public boolean isCacheable(String expressionObjectName) {
        return false;
    }
}
