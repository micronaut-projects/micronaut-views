package io.micronaut.docs

import com.google.template.soy.SoyFileSet
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.soy.SoyFileSetProvider

import jakarta.inject.Singleton


/**
 * Provide a SoyFileSet
 */
@Singleton
class ExampleSoyFileSetProvider implements SoyFileSetProvider {

    private final ViewsConfiguration viewsConfiguration
    ExampleSoyFileSetProvider(ViewsConfiguration viewsConfiguration) {
        this.viewsConfiguration = viewsConfiguration
    }
    /**
     * @return Soy file set to render templates with
     */
    @Override
    SoyFileSet provideSoyFileSet() {
        return SoyFileSet.builder()
            .add(new File(
                ExampleSoyFileSetProvider.class.getClassLoader().getResource(viewsConfiguration.getFolder() + "/home.soy").getFile()))
        .build()
    }
}
