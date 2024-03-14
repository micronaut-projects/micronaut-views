package io.micronaut.views.htmx.http;

import io.micronaut.context.annotation.Mapper;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;

@Requires(property = "spec.name", value = "HtmxRequestHeadersTypedRequestArgumentBinderTest")
@Singleton
public interface TodoItemMapper {
    @Mapper.Mapping(to = "completed", from = "#{false}")
    TodoItem toEntity(TodoItemFormData form);
}

