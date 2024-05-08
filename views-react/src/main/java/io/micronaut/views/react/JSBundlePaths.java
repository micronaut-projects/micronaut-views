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
package io.micronaut.views.react;

import io.micronaut.core.annotation.Internal;
import io.micronaut.views.ViewsConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Source;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static java.lang.String.format;

/**
 * Wraps the computation of where to find the JS for client and server.
 */
@Singleton
@Internal
class JSBundlePaths {
    final String bundleFileName;
    final Path bundlePath;

    private FileTime lastModified;
    private long lastModificationCheckTime = 0;

    @Inject
    JSBundlePaths(ViewsConfiguration viewsConfiguration, ReactViewsRendererConfiguration reactConfiguration) throws IOException {
        var folder = viewsConfiguration.getFolder();
        bundlePath = Path.of(folder).resolve(reactConfiguration.getServerBundlePath()).toAbsolutePath().normalize();
        if (!Files.exists(bundlePath)) {
            throw new FileNotFoundException(format("Server bundle %s could not be found. Check your %s property.", bundlePath, ReactViewsRendererConfiguration.PREFIX + ".server-bundle-path"));
        }
        bundleFileName = bundlePath.getFileName().toString();
        lastModified = Files.getLastModifiedTime(bundlePath);
    }

    synchronized boolean wasModified() {
        try {
            var now = System.currentTimeMillis();
            // Skip the check if it was done very recently. This makes the check disappear from
            // profiles under load.
            if (now - lastModificationCheckTime <= 500) {
                return false;
            }
            lastModificationCheckTime = now;

            FileTime time = Files.getLastModifiedTime(bundlePath);
            if (time.compareTo(lastModified) > 0) {
                lastModified = time;
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Source readServerBundle() throws IOException {
        try (var reader = Files.newBufferedReader(bundlePath)) {
            return Source.newBuilder("js", reader, bundleFileName)
                .mimeType("application/javascript+module")
                .build();
        }
    }
}
