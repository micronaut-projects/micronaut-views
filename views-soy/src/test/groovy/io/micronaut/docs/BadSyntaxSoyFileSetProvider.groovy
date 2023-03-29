package io.micronaut.docs

import com.google.template.soy.SoyFileSet
import io.micronaut.context.annotation.Requires
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.soy.SoyFileSetProvider
import jakarta.inject.Singleton

/**
 * Provide a SoyFileSet
 */
@Singleton
@Requires(property = "spec.name", value = "badsyntax")
class BadSyntaxSoyFileSetProvider implements SoyFileSetProvider {

    private final ViewsConfiguration viewsConfiguration

    BadSyntaxSoyFileSetProvider(ViewsConfiguration viewsConfiguration) {
        this.viewsConfiguration = viewsConfiguration
    }
    /**
     * @return Soy file set to render templates with
     */
    @Override
    SoyFileSet provideSoyFileSet() {
        return SoyFileSet.builder()
            .add(new File(
                BadSyntaxSoyFileSetProvider.class.getClassLoader().getResource(viewsConfiguration.getFolder() + "/badsyntax.soy").getFile()))
        .build()
    }
}
