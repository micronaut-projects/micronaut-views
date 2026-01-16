package io.micronaut.views.jstachio.pkginfo;

import io.jstach.jstache.JStache;
import org.jspecify.annotations.Nullable;

@JStache(path = "home")
public record HomeModel(@Nullable String username, boolean loggedIn) {
}
