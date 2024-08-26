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
import io.micronaut.inject.BeanType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.SandboxPolicy;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.util.List;
import java.util.stream.Stream;

import static io.micronaut.views.react.ReactJSBeanFactory.REACT_QUALIFIER;

/**
 * A bean that handles the Javascript {@link Context} object representing a loaded execution
 * environment usable by one thread at a time.
 */
@Internal
@Bean
class ReactJSContext implements AutoCloseable {
    // Symbols the user's server side bundle might supply us with.
    private static final List<String> IMPORT_SYMBOLS = List.of("React", "ReactDOMServer", "renderToString", "h");

    // Accessed from ReactViewsRenderer
    Context polyglotContext;
    Value render;
    Value ssrModule;

    private final Engine engine;
    private final HostAccess hostAccess;
    private final ApplicationContext applicationContext;
    private final ReactViewsRendererConfiguration configuration;

    @Inject
    ReactJSContext(ReactViewsRendererConfiguration configuration,
                   @Named(REACT_QUALIFIER) Engine engine,
                   @Named(REACT_QUALIFIER) HostAccess hostAccess,
                   ApplicationContext applicationContext) {
        this.configuration = configuration;
        this.engine = engine;
        this.hostAccess = hostAccess;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    void init() {
        polyglotContext = createContext();

        Value global = polyglotContext.getBindings("js");
        ssrModule = loadNamedModule(NamedSourceQualifier.ssr);

        // Take all the exports from the components bundle, and expose them to the render script.
        for (var name : ssrModule.getMemberKeys()) {
            global.putMember(name, ssrModule.getMember(name));
        }

        // Evaluate our JS-side framework specific render logic.
        Value renderModule = loadNamedModule(NamedSourceQualifier.renderScript);
        render = renderModule.getMember("ssr");
        if (render == null) {
            throw new IllegalArgumentException("Unable to look up ssr function in render script `%s`. Please make sure it is exported.".formatted(configuration.getRenderScript()));
        }
    }

    private Value loadNamedModule(NamedSourceQualifier sourceQualifier) {
        // This will return a cached, shared Source object.
        return polyglotContext.eval(applicationContext.createBean(Source.class, sourceQualifier));
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

    private record NamedSourceQualifier(String name) implements Qualifier<Source> {
        static NamedSourceQualifier ssr = new NamedSourceQualifier("react");
        static NamedSourceQualifier renderScript = new NamedSourceQualifier("react-render-script");

        @Override
        public <BT extends BeanType<Source>> Stream<BT> reduce(Class<Source> beanType, Stream<BT> candidates) {
            return candidates.filter(bt -> name.equals(bt.getBeanName().orElse(null)));
        }
    }
}
