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
package io.micronaut.views.fields.controllers.editupdate;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.validation.validator.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import static io.micronaut.views.fields.controllers.IdUtils.id;

public class RepositoryEditService<T, ID> implements EditService<T, ID> {
    private final CrudRepository<T, ID> repository;
    private final EditHtmlResource<T> createResource;

    private final Validator validator;
    RepositoryEditService(EditHtmlResource<T> createResource,
                          CrudRepository<T, ID> repository,
                          Validator validator) {
        this.createResource = createResource;
        this.repository = repository;
        this.validator = validator;
    }


    @Override
    @NonNull
    public Optional<T> findById(@NonNull ID id) {
        return repository.findById(id);
    }

    @Override
    @NonNull
    public URI update(@NonNull @NotNull @Valid T form) {
        Set<ConstraintViolation<T>> violationSet = validator.validate(form);
        if (!violationSet.isEmpty()) {
            throw new ConstraintViolationException(violationSet);
        }

        T entity = repository.update(form);
        Optional<String> idOptional = id(entity);
        return idOptional.isPresent() ?
            UriBuilder.of("/" + createResource.getName()).path(idOptional.get()).path("show").build() :
            UriBuilder.of("/" + createResource.getName()).path("list").build();
    }
}
