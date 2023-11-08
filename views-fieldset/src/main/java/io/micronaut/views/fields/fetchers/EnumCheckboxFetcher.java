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
package io.micronaut.views.fields.fetchers;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.fields.messages.Message;
import io.micronaut.views.fields.elements.Checkbox;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * {@link CheckboxFetcher} implementation for Enums.
 * @author Sergio del Amo
 * @since 4.1.0
 * @param <T> Field type
 */
@Experimental
@Singleton
public class EnumCheckboxFetcher<T> implements CheckboxFetcher<T> {
    private static final Logger LOG = LoggerFactory.getLogger(EnumCheckboxFetcher.class);

    @Override
    public List<Checkbox> generate(Class<T> type) {
        if (type.isEnum()) {
            return generateEnumCheckboxButtons((Class<? extends Enum>) type, null);
        } else {
            LOG.warn("Type {} is not an enum", type);
        }
        return Collections.emptyList();
    }

    @Override
    public List<Checkbox> generate(T instance) {
        if (instance.getClass().isEnum()) {
            return generateEnumCheckboxButtons((Class<? extends Enum>) instance.getClass(), (Enum) instance);
        } else {
            LOG.warn("instance {} is not an enum", instance.getClass());
        }
        return Collections.emptyList();
    }

    @NonNull
    private List<Checkbox> generateEnumCheckboxButtons(@NonNull Class<? extends Enum> type,
                                                       @Nullable Enum instance) {
        return EnumSet.allOf(type).stream()
            .map(it -> {
                Checkbox.Builder builder = checkboxButtonFromEnum(type, (Enum) it);
                if (instance != null && ((Enum<?>) it).name().equals(instance.name())) {
                    builder.checked(true);
                }
                return builder.build();
            })
            .toList();
    }

    @NonNull
    private Checkbox.Builder checkboxButtonFromEnum(@NonNull Class<? extends Enum> type,
                                                    @NonNull Enum instance) {
        String name = instance.name();
        return Checkbox.builder().id(name.toLowerCase()).value(name).label(Message.of(type, name));
    }
}
