package io.micronaut.views.fields.tests.signup

import io.micronaut.context.annotation.Requires
import io.micronaut.views.fields.controllers.CrudHtmlResource
import jakarta.inject.Named
import jakarta.inject.Singleton

@Requires(property = "spec.name", value = "UserCreatePageSpec")
@Named("user")
@Singleton
class UserCrudHtmlResource implements CrudHtmlResource<User> {
    @Override
    Class<User> getResourceClass() {
        User
    }
}
