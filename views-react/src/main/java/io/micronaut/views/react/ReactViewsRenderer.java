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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * <p>Instantiates GraalJS and uses it to render React components server side. See the user guide
 * to learn more about how to render React/Preact apps server side.</p>
 *
 * @param <PROPS> An introspectable bean type that will be fed to the ReactJS root component as props.
 * @param <REQUEST> Type of the HTTP request.
 */
@Singleton
public class ReactViewsRenderer<PROPS, REQUEST> implements ViewsRenderer<PROPS, REQUEST> {
    @Inject
    ReactViewsRendererConfiguration reactConfiguration;

    @Inject
    JSContextPool contextPool;

    @Inject
    JSBundlePaths jsBundlePaths;

    /**
     * Construct this renderer. Don't call it yourself, as Micronaut Views will set it up for you.
     */
    @Inject
    public ReactViewsRenderer() {
    }

    /**
     * Given a &lt;ViewName/&gt; and optionally an object that represents some props (can be a map
     * or introspectable object), returns hydratable HTML that can be booted on the client using
     * the React libraries.
     *
     * @param viewName The function or class name of the React component to use as the root. It should return an html root tag.
     * @param props    If non-null, will be exposed to the given component as React props.
     * @param request  The HTTP request object.
     */
    @Override
    public @NonNull Writable render(@NonNull String viewName, @Nullable PROPS props, @Nullable REQUEST request) {
        return writer -> {
            JSContext context = contextPool.acquire();
            try {
                if (jsBundlePaths.wasModified()) {
                    context.reinit();
                }

                render(viewName, props, writer, context);
            } finally {
                contextPool.release(context);
            }
        };
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        var context = contextPool.acquire();
        try {
            return context.moduleHasMember(viewName);
        } finally {
            contextPool.release(context);
        }
    }

    private void render(String componentName, PROPS props, Writer writer, JSContext context) {
        Value component = context.ssrModule.getMember(componentName);
        if (component == null) {
            throw new IllegalArgumentException("Component name %s wasn't exported from the SSR module.".formatted(componentName));
        }

        var renderCallback = new RenderCallback(writer);

        // TODO: Sandboxing. Do we need to deep clone the props here?
        @SuppressWarnings("unchecked")
        ProxyObject propsObj = isStringMap(props) ?
            ProxyObject.fromMap((Map<String, Object>) props) :
            new IntrospectableToPolyglotObject<>(context.polyglotContext, true, props);

        context.render.execute(component, propsObj, renderCallback, reactConfiguration.getClientBundleURL());
    }

    private boolean isStringMap(PROPS props) {
        if (props instanceof Map<?, ?> propsMap) {
            return propsMap.keySet().stream().allMatch(it -> it instanceof String);
        } else {
            return false;
        }
    }

    /**
     * Methods exposed to the ReactJS components and render scripts. Needs to be public to be
     * callable from the JS side.
     *
     * WARNING: These methods may be invoked by sandboxed code. Treat calls adversarially.
     *
     * @hidden
     */
    public static final class RenderCallback {
        private final Writer responseWriter;

        public RenderCallback(Writer responseWriter) {
            this.responseWriter = responseWriter;
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
