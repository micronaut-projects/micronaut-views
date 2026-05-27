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

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import io.micronaut.http.server.HttpServerConfiguration;
import io.micronaut.http.server.util.HttpHostResolver;
import jakarta.inject.Singleton;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

/**
 * Thymeleaf dialect for {@link RequestExpressionObject} instantiated via the {@link RequestExpressionObjectFactory}.
 *
 * @author Sergio del Amo
 * @since 6.1.0
 */
@Requires(beans = {HttpServerConfiguration.class, HttpHostResolver.class})
@Singleton
@Internal
public final class HttpServerRequestExpressionDialect extends AbstractDialect implements IExpressionObjectDialect {

    private static final String DIALECT_NAME = "Micronaut Web";

    private final IExpressionObjectFactory expressionObjectFactory;

    /**
     * @param httpServerConfiguration HTTP server configuration
     */
    HttpServerRequestExpressionDialect(HttpServerConfiguration httpServerConfiguration, HttpHostResolver httpHostResolver) {
        super(DIALECT_NAME);
        this.expressionObjectFactory = new RequestExpressionObjectFactory(httpHostResolver, httpServerConfiguration);
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return expressionObjectFactory;
    }
}
