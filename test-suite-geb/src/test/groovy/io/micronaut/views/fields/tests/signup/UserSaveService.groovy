package io.micronaut.views.fields.tests.signup

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.views.fields.controllers.createsave.SaveService
import jakarta.inject.Named
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

@Requires(property = "spec.name", value = "UserCreatePageSpec")
@Named("user")
@Singleton
class UserSaveService implements SaveService<User> {

    @Override
    URI save(@NonNull @NotNull @Valid User form) {
        return null
    }
}
