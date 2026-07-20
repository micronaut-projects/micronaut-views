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
import io.micronaut.core.io.scan.ClassPathResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.jspecify.annotations.Nullable;

/**
 * Loads Jinjava templates exclusively from the configured Views folder.
 */
@Internal
final class JinjavaResourceLocator implements ResourceLocator {

    private final ClassPathResourceLoader resourceLoader;
    private final String folder;

    JinjavaResourceLocator(ClassPathResourceLoader resourceLoader, String folder) {
        this.resourceLoader = resourceLoader;
        this.folder = folder;
    }

    @Override
    public String getString(String name, Charset charset, JinjavaInterpreter interpreter) throws IOException {
        String location = location(name);
        try (InputStream inputStream = resourceLoader.getResourceAsStream(location)
            .orElseThrow(() -> new ResourceNotFoundException(name))) {
            return new String(inputStream.readAllBytes(), charset);
        }
    }

    String location(String name) throws ResourceNotFoundException {
        String location = locationOrNull(name);
        if (location == null) {
            throw new ResourceNotFoundException(name);
        }
        return location;
    }

    @Nullable
    String locationOrNull(String name) {
        String normalized = name.replace('\\', '/');
        if (normalized.startsWith("/") || normalized.isEmpty()) {
            return null;
        }
        String[] segments = normalized.split("/", -1);
        for (String segment : segments) {
            if (segment.equals("..") || segment.isEmpty()) {
                return null;
            }
        }
        return folder + normalized;
    }

    ClassPathResourceLoader resourceLoader() {
        return resourceLoader;
    }
}
