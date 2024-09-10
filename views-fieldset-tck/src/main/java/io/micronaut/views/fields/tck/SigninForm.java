package io.micronaut.views.fields.tck;

import io.micronaut.core.annotation.Introspected;

@Introspected
public record SigninForm(String username, String password, boolean rememberMe) {
}
