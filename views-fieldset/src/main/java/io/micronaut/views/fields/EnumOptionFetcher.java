/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.fields;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * {@link OptionFetcher} implementation for Enums.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param <T> the field type
 */
@Experimental
@Singleton
public class EnumOptionFetcher<T> implements OptionFetcher<T> {

    private static final Logger LOG = LoggerFactory.getLogger(EnumOptionFetcher.class);

    @Override
    public List<Option> generate(Class<T> type) {
        if (type.isEnum()) {
            return generateEnumOptions((Class<? extends Enum>) type, null);
        } else {
            LOG.warn("Type {} is not an enum", type);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Option> generate(T instance) {
        if (instance.getClass().isEnum()) {
            return generateEnumOptions((Class<? extends Enum>) instance.getClass(), (Enum) instance);
        } else {
            LOG.warn("instance {} is not an enum", instance.getClass());
        }
        return Collections.emptyList();
    }

    @NonNull
    private List<Option> generateEnumOptions(@NonNull Class<? extends Enum> type,
                                             @Nullable Enum instance) {
        return EnumSet.allOf(type).stream()
            .map(it -> {
                Option.Builder builder = optionFromEnum(type, (Enum) it);
                if (instance != null && ((Enum<?>) it).name().equals(instance.name())) {
                    builder.selected(true);
                }
                return builder.build();
            })
            .toList();
    }

    @NonNull
    private Option.Builder optionFromEnum(@NonNull Class<? extends Enum> type, @NonNull Enum instance) {
        String name = instance.name();
        return Option.builder().value(name).label(labelForBeanProperty(type, name));
    }

    @NonNull
    private Message labelForBeanProperty(Class<? extends Enum> type, String name) {
        String code = type.getSimpleName().toLowerCase() + "." + name.toLowerCase();
        String defaultMessage = StringUtils.capitalize(name.toLowerCase().replaceAll("(.)([A-Z])", "$1 $2"));
        return Message.of(defaultMessage, code);
    }
}
