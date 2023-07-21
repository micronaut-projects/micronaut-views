package io.micronaut.views.jstachio;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.micronaut.core.annotation.Nullable;

//normally you would put the following annotation on package-info or module-info
@JStacheConfig(using = MicronautJStacheConfig.class) 
@JStache(path = "home")
public record HomeModel(@Nullable String username, boolean loggedIn) {

}
