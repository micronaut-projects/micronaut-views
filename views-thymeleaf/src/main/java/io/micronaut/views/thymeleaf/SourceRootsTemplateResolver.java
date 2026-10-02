/*
 * Copyright 2017-2026 original authors
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
package io.micronaut.views.thymeleaf;

import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsSourceRoots;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresource.FileTemplateResource;
import org.thymeleaf.templateresource.ITemplateResource;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * The template resolver of development mode: a template found under a {@link ViewsSourceRoots views source root}
 * is read from there, so that an edit is rendered without a copy step; any other is read from the class path.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceRootsTemplateResolver extends ClassLoaderTemplateResolver {

    private final ViewsSourceRoots sourceRoots;

    /**
     * @param sourceRoots The source roots
     * @param classLoader The class loader of the class path
     */
    SourceRootsTemplateResolver(ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        super(classLoader);
        this.sourceRoots = sourceRoots;
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration,
                                                        String ownerTemplate,
                                                        String template,
                                                        String resourceName,
                                                        String characterEncoding,
                                                        Map<String, Object> templateResolutionAttributes) {
        // the resource name is the prefix, the views folder, then the template and the suffix
        String prefix = getPrefix();
        if (prefix == null || resourceName.startsWith(prefix)) {
            String relative = prefix == null ? resourceName : resourceName.substring(prefix.length());
            Optional<Path> source = sourceRoots.resolve(relative, null);
            if (source.isPresent()) {
                return new FileTemplateResource(source.get().toFile(), characterEncoding);
            }
        }
        return super.computeTemplateResource(configuration, ownerTemplate, template, resourceName, characterEncoding, templateResolutionAttributes);
    }
}
