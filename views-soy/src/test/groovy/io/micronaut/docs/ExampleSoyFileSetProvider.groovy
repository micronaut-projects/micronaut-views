package io.micronaut.docs

import com.google.template.soy.SoyFileSet
import io.micronaut.views.soy.SoyFileSetProvider

import javax.inject.Singleton


/**
 * Provide a SoyFileSet
 */
@Singleton
class ExampleSoyFileSetProvider implements SoyFileSetProvider {
    /**
     * @return Soy file set to render templates with
     */
    @Override
    SoyFileSet provideSoyFileSet() {
        return SoyFileSet.builder()
            .add(new File(
                ExampleSoyFileSetProvider.class.getClassLoader().getResource("home.soy").getFile()))
        .build()
    }
}
