package com.projectcheckins.logging;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.http.util.HttpHeadersUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerFilter("/**")
class HttpHeaderLoggingFilter {
    private static final Logger LOG = LoggerFactory.getLogger(HttpHeaderLoggingFilter.class);
    @RequestFilter
    void filterRequest(HttpRequest<?> request) {
        LOG.debug("Request {} {}", request.getMethod(), request.getUri());
        HttpHeadersUtil.trace(LOG, request.getHeaders());
    }
}
