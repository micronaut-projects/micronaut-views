/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.velocity;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.TypeHint;

/**
 * Placeholder class to generate the meta-information needed by GraalVM.
 *
 * @author Iván López
 * @since 1.3.0
 */
@TypeHint(
        value = {
                org.apache.velocity.runtime.resource.ResourceManagerImpl.class,
                org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader.class,
                org.apache.velocity.runtime.resource.ResourceCacheImpl.class,
                org.apache.velocity.runtime.ParserPoolImpl.class,
                org.apache.velocity.runtime.directive.Break.class,
                org.apache.velocity.runtime.directive.Define.class,
                org.apache.velocity.runtime.directive.Evaluate.class,
                org.apache.velocity.runtime.directive.Foreach.class,
                org.apache.velocity.runtime.directive.Include.class,
                org.apache.velocity.runtime.directive.Macro.class,
                org.apache.velocity.runtime.directive.Parse.class,
                org.apache.velocity.runtime.directive.Stop.class,
                org.apache.velocity.util.introspection.TypeConversionHandlerImpl.class,
                org.apache.velocity.util.introspection.UberspectImpl.class},
        accessType = TypeHint.AccessType.ALL_PUBLIC
)
@Internal
@Experimental
class VelocityGraalSupport {
}
