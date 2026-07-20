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
import com.hubspot.jinjava.loader.ResourceLocator;
import com.hubspot.jinjava.loader.ResourceNotFoundException;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.IOUtils;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import jakarta.inject.Singleton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Loads Jinjava templates exclusively from the configured Views folder.
 */
@Singleton
@Internal
final class JinjavaResourceLocator implements ResourceLocator {
    private final ResourceLoader resourceLoader;

    JinjavaResourceLocator(ResourceLoader resourceLoader,
                           ViewsConfiguration viewsConfiguration) {
        this.resourceLoader = resourceLoader.forBase(ViewUtils.normalizeFolder(viewsConfiguration.getFolder()));
    }

    @Override
    public String getString(String name, Charset charset, JinjavaInterpreter interpreter) throws IOException {
        return getString(name, charset);
    }

    public String getString(String name, Charset charset) throws IOException {
        String normalizedName = ViewUtils.normalizeFile(name, null);
        if (normalizedName.contains("//")) {
            throw new ResourceNotFoundException(name);
        }
        return IOUtils.readText(new BufferedReader(new InputStreamReader(
            resourceLoader.getResourceAsStream(normalizedName)
                .orElseThrow(() -> new ResourceNotFoundException(name)), charset)));
    }

    public boolean exists(String name) {
        String normalizedName = ViewUtils.normalizeFile(name, null);
        return !normalizedName.contains("//") && resourceLoader.getResource(normalizedName).isPresent();
    }
}
