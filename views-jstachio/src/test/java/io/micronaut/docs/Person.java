package io.micronaut.docs;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.views.jstachio.MicronautJStacheConfig;

@Introspected
//normally you would put the following annotation on package-info or module-info
@JStacheConfig(using = MicronautJStacheConfig.class) 
@JStache(path = "home")
public record Person(@Nullable String username, boolean loggedIn) {

}
