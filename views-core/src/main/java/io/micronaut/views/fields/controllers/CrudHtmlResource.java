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
package io.micronaut.views.fields.controllers;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.views.fields.controllers.createsave.CreateHtmlResource;
import io.micronaut.views.fields.controllers.editupdate.EditHtmlResource;
import io.micronaut.views.fields.controllers.read.ReadHtmlResource;

public interface CrudHtmlResource<E> extends CreateHtmlResource<E>, EditHtmlResource<E>, ReadHtmlResource<E> {
    Class<E> getResourceClass();

    @Override
    default @NonNull String getName() {
        return getResourceClass().getSimpleName().toLowerCase();
    }

    @Override
    default Class<E> createClass() {
        return getResourceClass();
    }

    @Override
    default Class<E> editClass() {
        return getResourceClass();
    }

    @Override
    default Class<E> readClass() {
        return getResourceClass();
    }
}
