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
package io.micronaut.views.csp;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.Flowable;
import org.reactivestreams.Publisher;

import javax.annotation.Nullable;

import static io.micronaut.views.csp.CspConfiguration.DEFAULT_FILTER_PATH;
import static io.micronaut.views.csp.CspConfiguration.FILTER_PATH;

/**
 * <p>
 *      Provides support for <a href="https://www.w3.org/TR/CSP2/">Content Security Policy (CSP) Level 2</a>.
 * </p>
 *
 * <p>
 *      Content Security Policy, a mechanism web applications can use to mitigate a broad class of content
 *      injection vulnerabilities, such as cross-site scripting (XSS). Content Security Policy is a declarative
 *      policy that lets the authors (or server administrators) if a web application inform the client about
 *      the sources from which the application expects to load resources.
 * </p>
 *
 * <p>
 *     To mitigate XSS attacks, for example, a web application can declare that it only expects to load
 *     scripts from specific, trusted sources. This declaration allows the client to detect and block
 *     malicious scripts injected into the application by an attacker.
 * </p>
 *
 * <p>
 *     This implementation of {@link HttpServerFilter} writes one of the following HTTP headers:
 * </p>
 *
 * <ul>
 *     <li>Content-Security-Policy</li>
 *     <li>Content-Security-Policy-Report-Only</li>
 * </ul>
 *
 * @author Arul Dhesiaseelan
 * @since 1.1.0
 */
@Filter("${" + FILTER_PATH + ":" + DEFAULT_FILTER_PATH + "}")
public class CspFilter implements HttpServerFilter {

    public static final String CSP_HEADER = "Content-Security-Policy";
    public static final String CSP_REPORT_ONLY_HEADER = "Content-Security-Policy-Report-Only";
    public static final String NONCE_PROPERTY = "cspNonce";
    public static final String NONCE_TOKEN = "{#nonceValue}";

    protected final CspConfiguration cspConfiguration;

    /**
     * @param cspConfiguration The {@link CspConfiguration} instance
     */
    public CspFilter(CspConfiguration cspConfiguration) {
        this.cspConfiguration = cspConfiguration;
    }

    private @Nullable String nonceValue() {
        return cspConfiguration.isNonceEnabled() ? cspConfiguration.generateNonce() : null;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        final String nonce = nonceValue();
        return Flowable.fromPublisher(chain.proceed(request.setAttribute(NONCE_PROPERTY, nonce)))
                .doOnNext(response -> {
                    cspConfiguration.getPolicyDirectives()
                            .map(StringUtils::trimToNull)
                            .ifPresent(directives -> {
                        String header = cspConfiguration.isReportOnly() ? CSP_REPORT_ONLY_HEADER : CSP_HEADER;
                        final String headerValue;
                        if (directives.contains(NONCE_TOKEN)) {
                            if (nonce == null) {
                                // there is no nonce, but one was requested.
                                throw new IllegalArgumentException(
                                  "Must enable CSP nonce generation to use '" + NONCE_TOKEN + "' placeholder.");
                            }
                            headerValue = directives.replace(NONCE_TOKEN, nonce);
                        } else {
                            headerValue = directives;
                        }
                        response.getHeaders().add(header, headerValue);
                    });
                });
    }

}
