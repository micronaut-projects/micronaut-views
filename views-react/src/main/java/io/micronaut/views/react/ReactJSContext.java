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

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.Bean;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.BeanType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.graalvm.polyglot.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A bean that handles the Javascript {@link Context} object representing a loaded execution
 * environment usable by one thread at a time.
 */
@Internal
@Bean
class ReactJSContext implements AutoCloseable {
    // Symbols the user's server side bundle might supply us with.
    private static final List<String> IMPORT_SYMBOLS = List.of("React", "ReactDOMServer", "renderToString", "h");
    private final Engine engine;
    private final HostAccess hostAccess;
    private final ApplicationContext applicationContext;

    // Accessed from ReactViewsRenderer
    Context polyglotContext;
    Value render;
    Value ssrModule;

    private final ReactViewsRendererConfiguration configuration;

    @Inject
    ReactJSContext(ReactViewsRendererConfiguration configuration,
                   @Named("react") Engine engine,
                   @Named("react") HostAccess hostAccess,
                   ApplicationContext applicationContext) {
        this.configuration = configuration;
        this.engine = engine;
        this.hostAccess = hostAccess;
        this.applicationContext = applicationContext;
    }

    private static class ReactSourceQualifier implements Qualifier<Source> {
        @Override
        public <BT extends BeanType<Source>> Stream<BT> reduce(Class<Source> beanType, Stream<BT> candidates) {
            return candidates.filter(bt -> "react".equals(bt.getBeanName().orElse(null)));
        }

        static ReactSourceQualifier INSTANCE = new ReactSourceQualifier();
    }

    @PostConstruct
    void init() throws IOException {
        polyglotContext = createContext();

        Value global = polyglotContext.getBindings("js");
        // This will return a cached, shared Source object.
        ssrModule = polyglotContext.eval(applicationContext.createBean(Source.class, ReactSourceQualifier.INSTANCE));

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
            .engine(engine)
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw");

        if (configuration.getSandbox()) {
            contextBuilder
                .sandbox(SandboxPolicy.CONSTRAINED)
                .allowHostAccess(hostAccess);
        } else {
            // allowExperimentalOptions is here because as of the time of writing (August 2024)
            // the esm-eval-returns-exports option is experimental. That got fixed and this
            // can be removed once the base version of GraalJS is bumped to 24.1 or higher.
            contextBuilder
                .sandbox(SandboxPolicy.TRUSTED)
                .allowAllAccess(true)
                .allowExperimentalOptions(true);
        }

        try {
            return contextBuilder.build();
        } catch (ExceptionInInitializerError e) {
            // The catch handler is to work around a bug in Polyglot 24.0.0
            if (e.getCause().getMessage().contains("version compatibility check failed")) {
                throw new IllegalStateException("GraalJS version mismatch or it's missing. Please ensure you have added either org.graalvm.polyglot:js or org.graalvm.polyglot:js-community to your dependencies alongside Micronaut Views React, as it's up to you to select the best engine given your licensing constraints. See the user guide for more detail.");
            } else {
                throw e;
            }
        } catch (IllegalArgumentException e) {
            // We need esm-eval-returns-exports=true, but it's not compatible with the sandbox in this version of GraalJS.
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
