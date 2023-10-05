package io.micronaut.views.fields;

import io.micronaut.core.annotation.NonNull;

import java.util.List;

public interface OptionFetcher<T> {

    List<Option> generate(@NonNull Class<T> type);

    List<Option> generate(@NonNull T instance);
}
