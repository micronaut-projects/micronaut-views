package example.micronaut;

import io.micronaut.core.annotation.Internal;

@Internal
final class SoyTemplateBuildTimeInitializer {

    static {
        try {
            Class.forName("com.google.template.soy.jbcsrc.gen.sample")
                    .getMethod("home")
                    .invoke(null);
        } catch (Throwable ignored) {
        }
    }

    private SoyTemplateBuildTimeInitializer() {
    }

    static void ensureInitialized() {
    }
}
