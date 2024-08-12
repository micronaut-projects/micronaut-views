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
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.ResourceResolver;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

import static java.lang.String.format;

/**
 * Wraps the computation of where to find the JS for client and server.
 */
@Singleton
@Internal
class ReactJSBundlePaths {
    private static final Logger LOG = LoggerFactory.getLogger(ReactJSBundlePaths.class);

    // Source code file name, for JS stack traces.
    private final String bundleFileName;

    // URL of bundle file, could be a file:// or in a classpath jar.
    private final URL bundleURL;

    // If a file:// (during development), the path of that file. Used for hot reloads.
    @Nullable
    final Path bundlePath;

    @Inject
    ReactJSBundlePaths(ReactViewsRendererConfiguration reactConfiguration, ResourceResolver resolver) throws IOException {
        Optional<URL> bundlePathOpt = resolver.getResource(reactConfiguration.getServerBundlePath());
        if (bundlePathOpt.isEmpty()) {
            throw new FileNotFoundException(format("Server bundle %s could not be found. Check your %s property.", reactConfiguration.getServerBundlePath(), ReactViewsRendererConfiguration.PREFIX + ".server-bundle-path"));
        }
        bundleURL = bundlePathOpt.get();
        bundleFileName = bundleURL.getFile();
        if (bundleURL.getProtocol().equals("file")) {
            bundlePath = Path.of(bundleURL.getPath());
            LOG.info("Using server-side JS bundle from local disk: {}", bundlePath);
        } else {
            bundlePath = null;
        }
    }

    Source readServerBundle() throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(bundleURL.openStream()))) {
            return Source.newBuilder("js", reader, bundleFileName)
                .mimeType("application/javascript+module")
                .build();
        }
    }
}
