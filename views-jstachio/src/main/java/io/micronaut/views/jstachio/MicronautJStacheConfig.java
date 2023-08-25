/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.jstachio;

import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheInterfaces;
import io.jstach.jstache.JStacheName;
import io.jstach.jstache.JStachePath;

/**
 * Opinionated static JStache config for Micronaut.
 * @author agentgt
 * @since 4.1.0
 */
@JStacheConfig(
        pathing = @JStachePath(prefix = "views/", suffix = ".mustache"),
        naming= @JStacheName(suffix = "View"),
        interfacing = @JStacheInterfaces(templateImplements = MicronautJStacheTemplate.class)
        )
public enum MicronautJStacheConfig {
 // purposely empty
}
