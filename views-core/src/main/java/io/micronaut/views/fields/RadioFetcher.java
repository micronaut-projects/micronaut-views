package io.micronaut.views.fields;

import io.micronaut.core.annotation.NonNull;

import java.util.List;

public interface RadioFetcher<T> {

    List<Radio> generate(@NonNull Class<T> type);

    List<Radio> generate(@NonNull T instance);
}
