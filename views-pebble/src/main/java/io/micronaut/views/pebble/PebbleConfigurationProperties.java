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
package io.micronaut.views.pebble;

import com.mitchellbosecke.pebble.extension.escaper.EscapeFilter;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * {@link ConfigurationProperties} implementation of {@link PebbleConfiguration}.
 *
 * @author Ecmel Ercan
 * @since 2.2.0
 */
@ConfigurationProperties(PebbleConfigurationProperties.PREFIX)
public class PebbleConfigurationProperties implements PebbleConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".pebble";
    public static final String ENABLED = PREFIX + ".enabled";

    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_LOCALE = StringUtils.EMPTY_STRING;
    public static final String DEFAULT_EXTENSION = "html";
    public static final boolean DEFAULT_CACHE_ACTIVE = true;    
    public static final boolean DEFAULT_NEW_LINE_TRIMMING = true;
    public static final boolean DEFAULT_AUTO_ESCAPING = true;        
    public static final String DEFAULT_ESCAPING_STRATEGY = EscapeFilter.HTML_ESCAPE_STRATEGY;
    public static final boolean DEFAULT_STRICT_VARIABLES = false;
    public static final boolean DEFAULT_GREEDY_MATCH_METHOD = false;
    public static final boolean DEFAULT_ALLOW_OVERRIDE_CORE_OPERATORS = false;
    public static final boolean DEFAULT_LITERAL_DECIMALS_AS_INTEGERS = false;
    public static final boolean DEFAULT_LITERAL_NUMBERS_AS_BIG_DECIMALS = false;

    private boolean enabled = DEFAULT_ENABLED;
    private String defaultLocale = DEFAULT_LOCALE;
    private String defaultExtension = DEFAULT_EXTENSION;
    private boolean cacheActive = DEFAULT_CACHE_ACTIVE;
    private boolean newLineTrimming = DEFAULT_NEW_LINE_TRIMMING;
    private boolean autoEscaping = DEFAULT_AUTO_ESCAPING;
    private String defaultEscapingStrategy = DEFAULT_ESCAPING_STRATEGY;
    private boolean strictVariables = DEFAULT_STRICT_VARIABLES;
    private boolean greedyMatchMethod = DEFAULT_GREEDY_MATCH_METHOD;
    private boolean allowOverrideCoreOperators = DEFAULT_ALLOW_OVERRIDE_CORE_OPERATORS;
    private boolean literalDecimalsAsIntegers = DEFAULT_LITERAL_DECIMALS_AS_INTEGERS;
    private boolean literalNumbersAsBigDecimals = DEFAULT_LITERAL_NUMBERS_AS_BIG_DECIMALS;

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets whether the component is enabled. Default value ({@value #DEFAULT_ENABLED}).
     *
     * @param enabled Whether or not component is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * The default extension. Default value ({@value #DEFAULT_EXTENSION}).
     *
     * @param defaultExtension The extension
     */
    public void setDefaultExtension(String defaultExtension) {
        if (StringUtils.isNotEmpty(defaultExtension)) {
            this.defaultExtension = defaultExtension;
        }
    }

    @Override
    public boolean isCacheActive() {
        return cacheActive;
    }

    /**
     * Enable/disable all caches, i.e. cache used by the engine to store compiled PebbleTemplate
     * instances and tags cache. Default value ({@value #DEFAULT_CACHE_ACTIVE}).
     * 
     * @param cacheActive toggle to enable/disable all caches
     */
    public void setCacheActive(boolean cacheActive) {
        this.cacheActive = cacheActive;
    }

    @Override
    public boolean isNewLineTrimming() {
        return newLineTrimming;
    }

    /**
     * Changes the newLineTrimming setting of the PebbleEngine. By default, Pebble 
     * will trim a new line that immediately follows a Pebble tag. If set to false, then the 
     * first newline following a Pebble tag won't be trimmed.  All newlines will be preserved.
     * Default value ({@value #DEFAULT_NEW_LINE_TRIMMING}).
     *
     * @param newLineTrimming Whether or not the newline should be trimmed.
     */
    public void setNewLineTrimming(boolean newLineTrimming) {
        this.newLineTrimming = newLineTrimming;
    }

    @Override
    public boolean isAutoEscaping() {
        return autoEscaping;
    }

    /**
     * Sets whether or not escaping should be performed automatically.
     * Default value ({@value #DEFAULT_AUTO_ESCAPING}).
     *
     * @param autoEscaping The auto escaping setting
     */
    public void setAutoEscaping(boolean autoEscaping) {
        this.autoEscaping = autoEscaping;
    }

    @Override
    public String getDefaultEscapingStrategy() {
        return defaultEscapingStrategy;
    }

     /**
     * Sets the default escaping strategy of the built-in escaper extension.
     * Default value ({@value #DEFAULT_ESCAPING_STRATEGY}).
     *
     * @param defaultEscapingStrategy The name of the default escaping strategy
     */
    public void setDefaultEscapingStrategy(String defaultEscapingStrategy) {
        this.defaultEscapingStrategy = defaultEscapingStrategy;
    }

    @Override
    public boolean isStrictVariables() {
        return strictVariables;
    }

    /**
     * Changes the strictVariables setting of the PebbleEngine. 
     * Default value ({@value #DEFAULT_STRICT_VARIABLES}).
     *
     * @param strictVariables Whether or not strict variables is used
     */
    public void setStrictVariables(boolean strictVariables) {
        this.strictVariables = strictVariables;
    }

    @Override
    public boolean isGreedyMatchMethod() {
        return greedyMatchMethod;
    }

    /**
     * Enable/disable greedy matching mode for finding java method. Default is disabled. If enabled,
     * when can not find perfect method (method name, parameter length and parameter type are all
     * satisfied), reduce the limit of the parameter type, try to find other method which has
     * compatible parameter types.
     * Default value ({@value #DEFAULT_GREEDY_MATCH_METHOD}).
     * 
     * @param greedyMatchMethod toggle to enable/disable greedy match method
     * @see com.mitchellbosecke.pebble.utils.TypeUtils#compatibleCast(Object, Class)
     */
    public void setGreedyMatchMethod(boolean greedyMatchMethod) {
        this.greedyMatchMethod = greedyMatchMethod;
    }

    @Override
    public boolean isAllowOverrideCoreOperators() {
        return allowOverrideCoreOperators;
    }

    /**
     * Sets whether or not core operators overrides should be allowed.
     * Default value ({@value #DEFAULT_ALLOW_OVERRIDE_CORE_OPERATORS}).
     *
     * @param allowOverrideCoreOperators Whether or not core operators overrides should be allowed.
     */
    public void setAllowOverrideCoreOperators(boolean allowOverrideCoreOperators) {
        this.allowOverrideCoreOperators = allowOverrideCoreOperators;
    }

    @Override
    public boolean isLiteralDecimalsAsIntegers() {
        return literalDecimalsAsIntegers;
    }

    /**
     * Enable/disable treat literal decimal as Integer. 
     * Default value ({@value #DEFAULT_LITERAL_DECIMALS_AS_INTEGERS}), treated as Long.
     *
     * @param literalDecimalsAsIntegers toggle to enable/disable literal decimal treated as
     * integer
     */
    public void setLiteralDecimalsAsIntegers(boolean literalDecimalsAsIntegers) {
        this.literalDecimalsAsIntegers = literalDecimalsAsIntegers;
    }

    @Override
    public boolean isLiteralNumbersAsBigDecimals() {
        return literalNumbersAsBigDecimals;
    }

    /**
     * Enable/disable treat literal numbers as BigDecimals. 
     * Default value ({@value #DEFAULT_LITERAL_NUMBERS_AS_BIG_DECIMALS}), treated as Long/Double.
     * 
     * @param literalNumbersAsBigDecimals toggle to enable/disable literal numbers treated as
     * BigDecimals
     */
    public void setLiteralNumbersAsBigDecimals(boolean literalNumbersAsBigDecimals) {
        this.literalNumbersAsBigDecimals = literalNumbersAsBigDecimals;
    }

    @Override
    public String getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * The default locale. Default value ({@value #DEFAULT_LOCALE}).
     *
     * @param defaultExtension The extension
     */
    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}
