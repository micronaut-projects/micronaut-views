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

import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A bean that handles the Javascript {@link Context} object representing a loaded execution
 * environment usable by one thread at a time.
 */
@Internal
class JSContext implements AutoCloseable {
    // Symbols the user's server side bundle might supply us with.
    private static final List<String> IMPORT_SYMBOLS = List.of("React", "ReactDOMServer", "renderToString", "h");

    // Accessed from ReactViewsRenderer
    Context polyglotContext;
    Value render;
    Value ssrModule;

    private final CompiledJS compiledJS;
    private final ReactViewsRendererConfiguration configuration;
    private final JSSandboxing sandboxing;

    // What version of the on-disk bundle (considering file change events) we were loaded from.
    final int versionCounter;

    @Inject
    JSContext(CompiledJS compiledJS, ReactViewsRendererConfiguration configuration, JSSandboxing sandboxing, @Parameter int versionCounter) {
        this.compiledJS = compiledJS;
        this.configuration = configuration;
        this.sandboxing = sandboxing;
        this.versionCounter = versionCounter;
    }

    @PostConstruct
    void init() throws IOException {
        polyglotContext = createContext();

        Value global = polyglotContext.getBindings("js");
        ssrModule = polyglotContext.eval(compiledJS.getSource());

        // Take all the exports from the components bundle, and expose them to the render script.
        for (var name : ssrModule.getMemberKeys()) {
            global.putMember(name, ssrModule.getMember(name));
        }

        // Evaluate our JS-side framework specific render logic.
        Source source = loadRenderSource();
        Value renderModule = polyglotContext.eval(source);
        render = renderModule.getMember("ssr");
        if (render == null) {
            throw new IllegalArgumentException("Unable to look up ssr function in render script `%s`. Please make sure it is exported.".formatted(configuration.getRenderScript()));
        }
    }

    private Source loadRenderSource() throws IOException {
        String renderScriptName = configuration.getRenderScript();
        String fileName;
        String source;

        if (renderScriptName.startsWith("classpath:")) {
            var resourcePath = renderScriptName.substring("classpath:".length());
            // Even on Windows, classpath specs use /
            fileName = fileNameFromUNIXPath(resourcePath);
            try (var stream = getClass().getResourceAsStream(resourcePath)) {
                if (stream == null) {
                    throw new IllegalArgumentException("Render script not found on classpath: " + resourcePath);
                }
                source = new String(stream.readAllBytes(), UTF_8);
            }
        } else if (renderScriptName.startsWith("file:")) {
            var path = Path.of(renderScriptName.substring("file:".length()));
            if (!Files.exists(path)) {
                throw new IllegalArgumentException("Render script not found: " + renderScriptName);
            }
            fileName = path.normalize().toAbsolutePath().getFileName().toString();
            try (var stream = Files.newInputStream(path)) {
                source = new String(stream.readAllBytes(), UTF_8);
            }
        } else {
            throw new IllegalArgumentException("The renderScript name '%s' must begin with either `classpath:` or `file:`".formatted(renderScriptName));
        }

        return Source.newBuilder("js", source, fileName)
            .mimeType("application/javascript+module")
            .build();
    }

    private static @NonNull String fileNameFromUNIXPath(String resourcePath) {
        String fileName;
        var i = resourcePath.lastIndexOf('/');
        fileName = resourcePath.substring(i + 1);
        return fileName;
    }

    private Context createContext() {
        var contextBuilder = Context.newBuilder()
            .engine(compiledJS.engine)
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw");
        try {
            return sandboxing.configure(contextBuilder).build();
        } catch (ExceptionInInitializerError e) {
            // The catch handler is to work around a bug in Polyglot 24.0.0
            if (e.getCause().getMessage().contains("version compatibility check failed")) {
                throw new IllegalStateException("GraalJS version mismatch or it's missing. Please ensure you have added either org.graalvm.polyglot:js or org.graalvm.polyglot:js-community to your dependencies alongside Micronaut Views React, as it's up to you to select the best engine given your licensing constraints. See the user guide for more detail.");
            } else {
                throw e;
            }
        } catch (IllegalArgumentException e) {
            // We need esm-eval-returns-exports=true but it's not compatible with the sandbox in this version of GraalJS.
            if (e.getMessage().contains("Option 'js.esm-eval-returns-exports' is experimental")) {
                throw new IllegalStateException("The sandboxing feature requires a newer version of GraalJS. Please upgrade and try again, or disable the sandboxing feature.");
            } else {
                throw e;
            }
        }
    }

    boolean moduleHasMember(String memberName) {
        assert !IMPORT_SYMBOLS.contains(memberName) : "Should not query the server-side bundle for member name " + memberName;
        return ssrModule.hasMember(memberName);
    }

    @PreDestroy
    @Override
    public synchronized void close() {
        polyglotContext.close();
    }
}
