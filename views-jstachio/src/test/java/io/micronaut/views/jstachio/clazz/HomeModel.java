package io.micronaut.views.jstachio.clazz;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.jstachio.MicronautJStacheConfig;

@JStacheConfig(using = MicronautJStacheConfig.class)
@JStache(path = "home")
public record HomeModel(@Nullable String username, boolean loggedIn) {
}
