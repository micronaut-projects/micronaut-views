package io.micronaut.views.jstachio.pkginfo;

import io.jstach.jstache.JStache;
import io.micronaut.core.annotation.Nullable;

@JStache(path = "home")
public record HomeModel(@Nullable String username, boolean loggedIn) {
}
