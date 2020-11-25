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

import io.micronaut.core.util.Toggleable;

/**
 * Configuration for {@link PebbleViewsRenderer}.
 *
 * @author Ecmel Ercan
 * @since 2.1.1
 */
public interface PebbleConfiguration extends Toggleable {

    /**
     * @return Gets <code>defaultExtension</code> property
     */
    String getDefaultExtension();

    /**
     * @return Gets <code>defaultEscapingStrategy</code> property
     */
    String getDefaultEscapingStrategy();

    /**
     * @return Gets <code>strictVariables</code> property
     */    
    boolean isStrictVariables();

    /**
     * @return Gets <code>newLineTrimming</code> property
     */    
    boolean isNewLineTrimming();

    /**
     * @return Gets <code>cacheActive</code> property
     */    
    boolean isCacheActive();

   /**
     * @return Gets <code>autoEscaping</code> property
     */
    boolean isAutoEscaping();

    /**
     * @return Gets <code>greedyMatchMethod</code> property
     */    
    boolean isGreedyMatchMethod();

    /**
     * @return Gets <code>isAllowOverrideCoreOperators</code> property
     */    
    boolean isAllowOverrideCoreOperators();

        /**
     * @return Gets <code>literalDecimalsAsInteger</code> property
     */    
    boolean isLiteralDecimalsAsIntegers();

    /**
     * @return Gets <code>literalNumbersAsBigDecimals</code> property
     */    
    boolean isLiteralNumbersAsBigDecimals();
}
