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
package io.micronaut.views.jte;

import gg.jte.Content;
import gg.jte.ContentType;
import gg.jte.TemplateOutput;
import gg.jte.html.HtmlTemplateOutput;
import io.micronaut.context.annotation.Requires;
import org.jspecify.annotations.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsSourceRoots;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.Nullable;

import java.nio.file.Paths;

/**
 * JTE renderer constrained to text/plain.
 *
 * @param <T> type of input model.
 * @author edward3h
 * @since 3.1.0
 */
@Produces(MediaType.TEXT_PLAIN)
@Singleton
@Requires(classes = HttpRequest.class)
public class PlainJteViewsRenderer<T> extends JteViewsRenderer<T, HttpRequest<?>> {
    /**
     * @param viewsConfiguration Views Configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @deprecated Use {@link #PlainJteViewsRenderer(ViewsConfiguration, JteViewsRendererConfiguration, ViewsSourceRoots, ClassPathResourceLoader)} instead.
     */
    @Deprecated(since = "6.4.0")
    protected PlainJteViewsRenderer(ViewsConfiguration viewsConfiguration, JteViewsRendererConfiguration jteViewsRendererConfiguration) {
        this(viewsConfiguration, jteViewsRendererConfiguration, ViewsSourceRoots.none(), null);
    }

    /**
     * @param viewsConfiguration Views Configuration
     * @param jteViewsRendererConfiguration JTE specific configuration
     * @param sourceRoots The views source roots, whose templates are compiled at runtime in development mode
     * @param resourceLoader The class path resource loader, whose class loader templates compiled at runtime use
     * @since 6.4.0
     */
    @Inject
    protected PlainJteViewsRenderer(ViewsConfiguration viewsConfiguration,
                    JteViewsRendererConfiguration jteViewsRendererConfiguration,
                    ViewsSourceRoots sourceRoots,
                    @Nullable ClassPathResourceLoader resourceLoader) {
        super(viewsConfiguration, jteViewsRendererConfiguration, ContentType.Plain,
            Paths.get(jteViewsRendererConfiguration.getDynamicPath()).resolve("plain"),
            sourceRoots, resourceLoader != null ? resourceLoader.getClassLoader() : null);
    }

    @Override
    @NonNull
    TemplateOutput decorateOutput(@NonNull TemplateOutput output) {
        return new PlainHtmlTemplateOutput(output);
    }

    /**
     * If JTE templates were pre-compiled in Html mode, this workaround allows them to be rendered in Plain mode.
     */
    private static class PlainHtmlTemplateOutput implements HtmlTemplateOutput {
        private final TemplateOutput delegate;

        public PlainHtmlTemplateOutput(TemplateOutput delegate) {
            this.delegate = delegate;
        }

        @Override
        public void setContext(String tagName, String attributeName) {
            // no-op
        }

        @Override
        public void writeContent(String value) {
            delegate.writeContent(value);
        }

        @Override
        public void writeContent(String value, int beginIndex, int endIndex) {
            delegate.writeContent(value, beginIndex, endIndex);
        }

        @Override
        public void writeBinaryContent(byte[] value) {
            delegate.writeBinaryContent(value);
        }

        @Override
        public void writeUserContent(String value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(Enum<?> value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(Content content) {
            delegate.writeUserContent(content);
        }

        @Override
        public void writeUserContent(boolean value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(byte value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(short value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(int value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(long value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(float value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(double value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(char value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(Boolean value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(Number value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUserContent(Character value) {
            delegate.writeUserContent(value);
        }

        @Override
        public void writeUnsafeContent(String value) {
            delegate.writeUnsafeContent(value);
        }
    }
}
