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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Singleton
public class EnumRadioFetcher<T> implements RadioFetcher<T> {
    private static final Logger LOG = LoggerFactory.getLogger(EnumOptionFetcher.class);

    @Override
    public List<Radio> generate(Class<T> type) {
        if (type.isEnum()) {
            return generateEnumRadioButtons((Class<? extends Enum>) type, null);
        } else {
            LOG.warn("Type {} is not an enum", type);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Radio> generate(T instance) {
        if (instance.getClass().isEnum()) {
            return generateEnumRadioButtons((Class<? extends Enum>) instance.getClass(), (Enum) instance);
        } else {
            LOG.warn("instance {} is not an enum", instance.getClass());
        }
        return Collections.emptyList();
    }

    @NonNull
    private <T extends Enum> List<Radio> generateEnumRadioButtons(@NonNull Class<? extends Enum> type,
                                                                  @Nullable Enum instance) {
        return EnumSet.allOf(type).stream()
            .map(it -> {
                Radio.Builder builder = radioButtonFromEnum(type, (Enum) it);
                if (instance != null && ((Enum<?>) it).name().equals(instance.name())) {
                    builder.checked(true);
                }
                return builder.build();
            })
            .toList();
    }

    @NonNull
    private Radio.Builder radioButtonFromEnum(@NonNull Class<? extends Enum> type, @NonNull Enum instance) {
        String name = instance.name();
        return Radio.builder().id(name.toLowerCase()).value(name).label(labelForBeanProperty(type, name));
    }

    @NonNull
    private <T> Message labelForBeanProperty(Class<T> type, String name) {
        return Message.of(type.getSimpleName().toLowerCase() + "." + name.toLowerCase(),
            StringUtils.capitalize(name.toLowerCase().replaceAll("(.)([A-Z])", "$1 $2")));
    }
}
