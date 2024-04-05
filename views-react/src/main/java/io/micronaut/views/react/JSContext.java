package io.micronaut.views.react;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.io.IOException;
import java.util.List;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

@Singleton
class JSContext implements AutoCloseable {
    @Inject
    JSEngineLogHandler engineLogHandler;

    @Inject
    JSBundlePaths jsBundlePaths;

    @Inject
    CompiledJS compiledJS;

    Context context;
    Value render;

    // Symbols the user's server side bundle might supply us with.
    private static final List<String> IMPORT_SYMBOLS = List.of("React", "ReactDOMServer", "renderToString", "h");

    Value ssrModule;

    @PostConstruct
    void init() throws IOException {
        context = createContext();

        Value global = context.getBindings("js");
        ssrModule = context.eval(compiledJS.source);

        if (!ssrModule.hasMember("React") && !ssrModule.hasMember("h"))
            throw new IllegalStateException(format("Your %s bundle must re-export the React module or the 'h' symbol from Preact.", jsBundlePaths.bundleFileName));

        // Make sure we can eval some code that uses the React APIs.
        for (var name : IMPORT_SYMBOLS) {
            if (!ssrModule.hasMember(name))
                continue;
            global.putMember(name, ssrModule.getMember(name));
        }

        // Evaluate our JS-side render logic to load it into the context.
        Value renderModule = context.eval(
            Source.newBuilder("js", loadRenderSource(), "render.js")
                .mimeType("application/javascript+module")
                .build()
        );
        render = renderModule.getMember("ssr");
    }

    private String loadRenderSource() throws IOException {
        try (var stream = getClass().getResourceAsStream("render.js")) {
            assert stream != null;
            return new String(stream.readAllBytes(), UTF_8);
        }
    }

    private Context createContext() {
        Logger jsLogger = LoggerFactory.getLogger("js");

        // TODO: Sandboxing is currently incompatible with esm-eval-returns-exports.
        //       If the problem won't be fixed soon, rework to avoid depending on that feature so
        //       the sandbox can be enabled.

        Context.Builder contextBuilder = Context.newBuilder()
            .engine(compiledJS.engine)
            .allowExperimentalOptions(true)
            .logHandler(engineLogHandler)
            .allowAllAccess(true)
            // .sandbox(SandboxPolicy.CONSTRAINED)
            .option("js.esm-eval-returns-exports", "true")
            .option("js.unhandled-rejections", "throw")
            .out(new OutputStreamToSLF4J(jsLogger, Level.INFO))
            .err(new OutputStreamToSLF4J(jsLogger, Level.ERROR));

        try {
            return contextBuilder.build();
        } catch (ExceptionInInitializerError e) {
            // The catch handler is to work around a bug in Polyglot 24.0.0
            if (e.getCause().getMessage().contains("version compatibility check failed")) {
                throw new IllegalStateException("GraalJS version mismatch or it's missing. Please ensure you have added either org.graalvm.polyglot:js or org.graalvm.polyglot:js-community to your dependencies alongside Micronaut Views React, as it's up to you to select the best engine given your licensing constraints. See the user guide for more detail.");
            } else throw e;
        }
    }

    boolean moduleHasMember(String memberName) {
        assert !IMPORT_SYMBOLS.contains(memberName) : "Should not query the server-side bundle for member name " + memberName;
        return ssrModule.hasMember(memberName);
    }


    @PreDestroy
    @Override
    public synchronized void close() {
        context.close();
    }

    public void reinit() throws IOException {
        close();
        init();
    }
}
