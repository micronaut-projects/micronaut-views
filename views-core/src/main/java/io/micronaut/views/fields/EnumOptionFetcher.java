package io.micronaut.views.fields;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

@Named("enum")
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
    private <T extends Enum> List<Option> generateEnumOptions(@NonNull Class<? extends Enum> type,
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
    private <T> Message labelForBeanProperty(Class<T> type, String name) {
        return Message.of(type.getSimpleName().toLowerCase() + "." + name.toLowerCase(),
            StringUtils.capitalize(name.toLowerCase().replaceAll("(.)([A-Z])", "$1 $2")));
    }
}
