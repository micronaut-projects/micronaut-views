package io.micronaut.views.jstachio.clazz;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStacheName;
import io.jstach.jstache.JStachePath;
import io.jstach.jstachio.Template;

@JStacheConfig(
        pathing = @JStachePath(prefix = "views/", suffix = ".mustache"),
        naming = @JStacheName(suffix = "View"),
        interfacing = @JStacheInterfaces(templateImplements = Template.class)
        )
public enum MicronautJStacheConfig {
}
