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
package io.micronaut.views.fields.controllers.read;

import io.micronaut.data.repository.CrudRepository;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.fields.controllers.editupdate.EditHtmlResource;
import io.micronaut.views.fields.controllers.editupdate.EditService;

import java.net.URI;
import java.util.Optional;

import static io.micronaut.views.fields.controllers.IdUtils.id;

public class RepositoryReadService<T, ID> implements ReadService<T, ID> {
    private final CrudRepository<T, ID> repository;
    private final ReadHtmlResource<T> resource;
    RepositoryReadService(ReadHtmlResource<T> resource,
                          CrudRepository<T, ID> repository) {
        this.resource = resource;
        this.repository = repository;
    }


    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }
}
