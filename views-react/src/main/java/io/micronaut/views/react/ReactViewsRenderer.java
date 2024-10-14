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

import io.micronaut.context.exceptions.BeanInstantiationException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.exceptions.MessageBodyException;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.react.truffle.IntrospectableToTruffleAdapter;
import io.micronaut.views.react.util.BeanPool;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * <p>Instantiates GraalJS and uses it to render React components server side. See the user guide
 * to learn more about how to render React/Preact apps server side.</p>
 *
 * @param <PROPS> An introspectable bean type that will be fed to the ReactJS root component as props.
 */
@Singleton
class ReactViewsRenderer<PROPS> implements ViewsRenderer<PROPS, HttpRequest<?>> {
    private final BeanPool<ReactJSContext> beanPool;
    private final ReactViewsRendererConfiguration reactViewsRendererConfiguration;

    ReactViewsRenderer(BeanPool<ReactJSContext> beanPool, ReactViewsRendererConfiguration reactViewsRendererConfiguration) {
        this.beanPool = beanPool;
        this.reactViewsRendererConfiguration = reactViewsRendererConfiguration;
    }

    /**
     * Given a &lt;ViewName/&gt; and optionally an object that represents some props (can be a map
     * or introspectable object), returns hydratable HTML that can be booted on the client using
     * the React libraries.
     *
     * @param viewName The function or class name of the React component to use as the root. It should return an HTML root tag.
     * @param props    If non-null, will be exposed to the given component as React props.
     * @param request  The HTTP request object.
     */
    @Override
    public @NonNull Writable render(@NonNull String viewName, @Nullable PROPS props, @Nullable HttpRequest<?> request) {
        return writer -> {
            try {
                beanPool.useContext(handle -> {
                    render(viewName, props, writer, handle.get(), request);
                    return null;
                });
            } catch (BeanInstantiationException e) {
                throw e;
            } catch (Exception e) {
                // If we don't wrap and rethrow, the exception is swallowed and the request hangs.
                throw new MessageBodyException("Could not render component " + viewName, e);
            }
        };
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return beanPool.useContext(handle -> handle.get().moduleHasMember(viewName));
    }

    private void render(String componentName, PROPS props, Writer writer, ReactJSContext context, @Nullable HttpRequest<?> request) {
        Value component = context.ssrModule.getMember(componentName);
        if (component == null) {
            throw new IllegalArgumentException("Component name %s wasn't exported from the SSR module.".formatted(componentName));
        }

        var renderCallback = new RenderCallback(writer, request);

        // We wrap the props object so we can use Micronaut's compile-time reflection implementation.
        // This should be more native-image friendly (no need to write reflection config files), and
        // might also be faster.
        Value guestProps = IntrospectableToTruffleAdapter.wrap(context.polyglotContext, props);
        context.render.executeVoid(component, guestProps, renderCallback, reactViewsRendererConfiguration.getClientBundleURL(), request);
    }


    /**
     * Methods exposed to the ReactJS components and render scripts. Needs to be public to be
     * callable from the JS side.
     * <p>
     * WARNING: These methods may be invoked by sandboxed code. Treat calls adversarially and
     * mark methods with @HostAccess.Export to ensure they're visible inside the sandbox.
     *
     * @hidden
     */
    public static final class RenderCallback {
        private final Writer responseWriter;
        private final @Nullable HttpRequest<?> request;

        RenderCallback(Writer responseWriter, HttpRequest<?> request) {
            this.responseWriter = responseWriter;
            this.request = request;
        }

        @HostAccess.Export
        @Nullable
        public String url() {
            if (request == null) {
                return null;
            }
            return request.getUri().toString();
        }

        @HostAccess.Export
        public void write(String html) {
            try {
                responseWriter.write(html);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @HostAccess.Export
        public void write(int[] unsignedBytes) {
            try {
                byte[] bytes = new byte[unsignedBytes.length];
                for (int i = 0; i < unsignedBytes.length; i++) {
                    bytes[i] = (byte) unsignedBytes[i];
                }
                responseWriter.write(new String(bytes, StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
