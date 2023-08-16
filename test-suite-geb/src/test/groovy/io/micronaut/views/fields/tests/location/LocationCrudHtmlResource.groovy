package io.micronaut.views.fields.tests.location

import io.micronaut.context.annotation.Requires
import io.micronaut.views.fields.controllers.CrudHtmlResource
import io.micronaut.views.fields.tests.books.AuthorEntity
import jakarta.inject.Named
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = "LocationCreateSpec")
@Named("location")
@Singleton
class LocationCrudHtmlResource implements CrudHtmlResource<Location> {
    @Override
    Class<Location> getResourceClass() {
        Location
    }
}
