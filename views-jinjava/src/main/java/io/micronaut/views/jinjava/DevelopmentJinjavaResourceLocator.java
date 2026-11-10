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
package io.micronaut.views.jinjava;

import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.env.DevelopmentActive;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The Jinjava resource locator of development mode: a template of the views source roots, such as one another
 * includes, is read from there, so that an edit is rendered without a copy step.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
@Singleton
@DevelopmentActive
@Replaces(JinjavaResourceLocator.class)
final class DevelopmentJinjavaResourceLocator extends JinjavaResourceLocator {
    private final ViewsSourceRoots sourceRoots;

    DevelopmentJinjavaResourceLocator(ClassPathResourceLoader resourceLoader,
                                      ViewsConfiguration viewsConfiguration,
                                      ViewsSourceRoots sourceRoots) {
        super(resourceLoader, viewsConfiguration);
        this.sourceRoots = sourceRoots;
    }

    @Override
    public String getString(String name, Charset charset, JinjavaInterpreter interpreter) throws IOException {
        Optional<Path> source = sourceRoots.resolve(name, null);
        if (source.isPresent()) {
            return Files.readString(source.get(), charset);
        }
        return super.getString(name, charset, interpreter);
    }
}
