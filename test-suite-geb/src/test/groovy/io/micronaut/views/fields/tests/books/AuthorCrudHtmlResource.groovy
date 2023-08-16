package io.micronaut.views.fields.tests.books

import io.micronaut.context.annotation.Requires
import io.micronaut.views.fields.controllers.CrudHtmlResource
import io.micronaut.views.fields.controllers.createsave.CreateHtmlResource
import jakarta.inject.Named
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = "AuthorCreateSpec")
@Named("author")
@Singleton
class AuthorCrudHtmlResource implements CrudHtmlResource<AuthorEntity> {
    @Override
    Class<AuthorEntity> getResourceClass() {
        AuthorEntity
    }

    @Override
    String getName() {
        "author"
    }
}
