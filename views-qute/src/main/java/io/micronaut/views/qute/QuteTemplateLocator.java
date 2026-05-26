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
package io.micronaut.views.qute;

import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import io.quarkus.qute.StringTemplateLocation;
import io.quarkus.qute.TemplateLocator;
import io.quarkus.qute.Variant;
import org.jspecify.annotations.NonNull;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Locates Qute templates from the Micronaut Views classpath folder.
 *
 * @author Sergio del Amo
 * @since 6.1.0
 */
public class QuteTemplateLocator implements TemplateLocator {

    private static final String EXTENSION_HTML = "html";
    private static final String EXTENSION_HTM = "htm";
    private static final String EXTENSION_XHTML = "xhtml";
    private static final String EXTENSION_TXT = "txt";
    private static final String EXTENSION_XML = "xml";
    private static final String EXTENSION_JSON = "json";
    private static final String APPLICATION_XHTML = "application/xhtml+xml";

    private final ClassPathResourceLoader resourceLoader;
    private final String folder;
    private final String defaultExtension;

    /**
     * @param resourceLoader Classpath resource loader
     * @param viewsConfiguration Views configuration
     * @param quteConfiguration Qute views configuration
     */
    public QuteTemplateLocator(ClassPathResourceLoader resourceLoader,
                               ViewsConfiguration viewsConfiguration,
                               QuteViewsRendererConfiguration quteConfiguration) {
        this.resourceLoader = resourceLoader;
        this.folder = ViewUtils.normalizeFolder(viewsConfiguration.getFolder());
        this.defaultExtension = quteConfiguration.getDefaultExtension();
    }

    @Override
    public Optional<TemplateLocation> locate(String id) {
        return normalizeTemplate(id)
            .flatMap(location -> resourceLoader.getResource(location)
                .flatMap(resource -> templateLocation(resource, location)));
    }

    /**
     * @param id Template id
     * @return true if the template exists
     */
    public boolean exists(@NonNull String id) {
        return normalizeTemplate(id)
            .flatMap(resourceLoader::getResource)
            .isPresent();
    }

    private Optional<TemplateLocation> templateLocation(URL resource, String location) {
        try (InputStream inputStream = resource.openStream()) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String extension = extension(location);
            return Optional.of(new StringTemplateLocation(template, variant(extension), Optional.of(resource.toURI())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<String> normalizeTemplate(String id) {
        if (StringUtils.isEmpty(id)) {
            return Optional.empty();
        }
        String normalized = id.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        for (String segment : normalized.split("/")) {
            if ("..".equals(segment)) {
                return Optional.empty();
            }
        }
        normalized = ViewUtils.normalizeFile(normalized, defaultExtension);
        if (StringUtils.isEmpty(normalized)) {
            return Optional.empty();
        }
        if (extension(normalized).isEmpty()) {
            normalized = normalized + ViewUtils.EXTENSION_SEPARATOR + defaultExtension;
        }
        return Optional.of(folder + normalized);
    }

    private static String extension(String path) {
        int index = path.lastIndexOf(ViewUtils.EXTENSION_SEPARATOR);
        return index < 0 ? "" : path.substring(index + 1);
    }

    private static Optional<Variant> variant(String extension) {
        return switch (extension) {
            case EXTENSION_HTML -> Optional.of(Variant.forContentType(Variant.TEXT_HTML));
            case EXTENSION_HTM -> Optional.of(Variant.forContentType(Variant.TEXT_HTML));
            case EXTENSION_XHTML -> Optional.of(Variant.forContentType(APPLICATION_XHTML));
            case EXTENSION_TXT -> Optional.of(Variant.forContentType(Variant.TEXT_PLAIN));
            case EXTENSION_XML -> Optional.of(Variant.forContentType(Variant.TEXT_XML));
            case EXTENSION_JSON -> Optional.of(Variant.forContentType(Variant.APPLICATION_JSON));
            default -> Optional.empty();
        };
    }
}
