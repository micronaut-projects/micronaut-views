package io.micronaut.views.jstachio.pkginfo;

import io.jstach.jstache.JStache;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;

@Introspected
@JStache(path = "home")
public record HomeModel(@Nullable String username, boolean loggedIn) {
}
