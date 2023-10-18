package io.micronaut.views.fields;

import io.micronaut.core.annotation.NonNull;

import java.util.List;

public interface CheckboxFetcher<T> {

    List<Checkbox> generate(@NonNull Class<T> type);

    List<Checkbox> generate(@NonNull T instance);
}
