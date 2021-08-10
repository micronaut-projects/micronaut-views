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

import com.google.common.collect.ImmutableMap;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.NonNull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Data class representing render flow context for a single Soy template render execution. Holds regular render values
 * and injected render values.
 *
 * @author Sam Gammon (sam@momentum.io)
 * @since 1.3.4
 */
@Immutable
@SuppressWarnings("WeakerAccess")
public final class SoyContext implements SoyContextMediator {
    /** Properties for template render. */
    private final @NonNull Map<String, Object> props;

    /** Injected values for template render. */
    private final @NonNull Map<String, Object> injected;

    /** Naming map provider. Overrides globally-installed provider if set. */
    private final @Nullable SoyNamingMapProvider overrideNamingMap;

    /**
     * Private constructor. See static factory methods to create a new `SoyContext`.
     *
     * @param props Properties/values to make available via `@param` declarations.
     * @param injected Properties/values to make available via `@inject` declarations.
     * @param overrideNamingMap Naming map to apply, overrides any global rewrite map.
     */
    private SoyContext(@NonNull Map<String, Object> props,
                       @NonNull Map<String, Object> injected,
                       @NonNull Optional<SoyNamingMapProvider> overrideNamingMap) {
        this.props = ImmutableMap.copyOf(props);
        this.injected = ImmutableMap.copyOf(injected);
        this.overrideNamingMap = overrideNamingMap;
    }

    /**
     * Create a new `SoyContext` object from a map of properties, additionally specifying any properties made available
     * via `@inject` declarations in the template to be rendered.
     *
     * @param props Properties to attach to this Soy render context.
     * @param injected Injected properties and values to attach to this context.
     * @param overrideNamingMap Naming map to use for this execution, if renaming is enabled.
     * @return Instance of `SoyContext` that holds the properties specified.
     * @throws IllegalArgumentException If any provided argument is `null`. Pass an empty map or an empty `Optional`.
     */
    public static SoyContext fromMap(@NonNull Map<String, Object> props,
                                     @NonNull Optional<Map<String, Object>> injected,
                                     @NonNull Optional<SoyNamingMapProvider> overrideNamingMap) {
        //noinspection ConstantConditions,OptionalAssignedToNull
        if (props == null || injected == null || overrideNamingMap == null) {
            throw new IllegalArgumentException(
                    "Must provide empty maps instead of `null` to `SoyContext`.");
        }
        return new SoyContext(
                props,
                injected.orElse(Collections.emptyMap()),
                overrideNamingMap);
    }

    // -- Public API -- //

    /**
     * Retrieve properties which should be made available via regular, declared `@param` statements.
     *
     * @return Map of regular template properties.
     */
    @Override @NonNull
    public Map<String, Object> getProperties() {
        return props;
    }

    /**
     * Retrieve properties and values that should be made available via `@inject`, additionally specifying an optional
     * overlay of properties to apply before returning.
     *
     * @param framework Properties injected by the framework.
     * @return Map of injected properties and their values.
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public @NonNull Map<String, Object> getInjectedProperties(@NonNull Map<String, Object> framework) {
        ImmutableMap.Builder<String, Object> merged = ImmutableMap.builderWithExpectedSize(injected.size() + framework.size());
        merged.putAll(injected);
        merged.putAll(framework);
        return merged.build();
    }

    /**
     * Specify a Soy renaming map which overrides the globally-installed map, if any. Renaming must still be activated via
     * config, or manually, for the return value of this method to have any effect.
     *
     * @return {@link SoyNamingMapProvider} that should be used for this render routine.
     */
    @Override @NonNull
    public Optional<SoyNamingMapProvider> overrideNamingMap() {
        return overrideNamingMap;
    }

}
