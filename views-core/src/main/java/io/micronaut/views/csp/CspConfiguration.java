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
package io.micronaut.views.csp;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.Toggleable;
import io.micronaut.views.ViewsConfigurationProperties;

import javax.annotation.Nullable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

/**
 * Defines CSP configuration properties.
 *
 * @author Arul Dhesiaseelan
 * @since 1.1
 */
@ConfigurationProperties(CspConfiguration.PREFIX)
public class CspConfiguration implements Toggleable {
    /**
     * Length of generated CSP nonce values.
     */
    public static final int NONCE_LENGTH = 16;

    /**
     * Secure random to use for CSP nonce values.
     */
    public static final Random DEFAULT_RANDOM;

    /**
     * Default Base64 encoder.
     */
    public static final Base64.Encoder BASE64_ENCODER =
      Base64.getEncoder().withoutPadding();

    static {
        try {
            DEFAULT_RANDOM = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException rxe) {
            throw new RuntimeException(rxe);
        }
    }

    /**
     * The prefix for csp configuration.
     */
    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".csp";

    /**
     * The path for endpoints settings.
     */
    public static final String FILTER_PATH = PREFIX + ".filter-path";
    public static final boolean DEFAULT_ENABLED = false;
    public static final boolean DEFAULT_REPORT_ONLY = false;
    public static final boolean DEFAULT_ENABLE_NONCE = false;
    public static final String DEFAULT_FILTER_PATH = "/**";

    private boolean enabled = DEFAULT_ENABLED;
    private String policyDirectives;
    private boolean reportOnly = DEFAULT_REPORT_ONLY;
    private boolean generateNonce = DEFAULT_ENABLE_NONCE;
    private String filterPath = DEFAULT_FILTER_PATH;
    private Random randomEngine = DEFAULT_RANDOM;

    /**
     * @return Whether csp headers will be sent
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return The policy directives
     */
    public Optional<String> getPolicyDirectives() {
        return Optional.of(policyDirectives);
    }

    /**
     * @return The random number generator to use for CSP nonce values
     */
    public Random getRandomEngine() {
        return randomEngine;
    }

    /**
     * @return Whether the report only header should be set
     */
    public boolean isReportOnly() {
        return reportOnly;
    }

    /**
     * @return Whether nonce generation is enabled for each request/response cycle
     */
    public boolean isNonceEnabled() {
        return generateNonce;
    }

    /**
     * Sets whether CSP is enabled. Default {@value #DEFAULT_ENABLED}.
     *
     * @param enabled True if CSP is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Sets the policy directives.
     * @param policyDirectives CSP policy directives
     */
    public void setPolicyDirectives(@Nullable String policyDirectives) {
        this.policyDirectives = policyDirectives;
    }

    /**
     * Sets the random number generator to use for nonce values.
     * @param randomEngine random number generator.
     */
    public void setRandomEngine(Random randomEngine) {
        this.randomEngine = randomEngine;
    }

    /**
     * If true, the Content-Security-Policy-Report-Only header will be sent instead
     * of Content-Security-Policy. Default {@value #DEFAULT_REPORT_ONLY}.
     *
     * @param reportOnly set to true for reporting purpose only
     */
    public void setReportOnly(boolean reportOnly) {
        this.reportOnly = reportOnly;
    }

    /**
     * If true, the CSP header will contain a generated nonce that is made available
     * to view renderers. The nonce should change for each request/response cycle and
     * can be used by views to authorize inlined script blocks.
     *
     * @param generateNonce set to true to enable generation of nonces
     */
    public void setGenerateNonce(boolean generateNonce) {
        this.generateNonce = generateNonce;
    }

    /**
     * @return The path the CSP filter should apply to
     */
    public String getFilterPath() {
        return filterPath;
    }

    /**
     * Sets the path the CSP filter should apply to. Default value {@value #DEFAULT_FILTER_PATH}.
     *
     * @param filterPath The filter path
     */
    public void setFilterPath(String filterPath) {
        this.filterPath = filterPath;
    }

    /**
     * Generate a nonce value for use in a Content-Security-Policy header, which
     * is usable for one request/response cycle.
     *
     * A good guide for generating nonce values:
     * https://csp.withgoogle.com/docs/faq.html#generating-nonces
     *
     * @return Base64-encoded random nonce value.
     */
    public String generateNonce() {
        byte[] randomBytes = new byte[NONCE_LENGTH];
        randomEngine.nextBytes(randomBytes);
        return BASE64_ENCODER.encodeToString(randomBytes);
    }
}
