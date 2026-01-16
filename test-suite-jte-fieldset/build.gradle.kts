plugins {
    id("io.micronaut.build.internal.views-fieldset-tck")
    alias(libs.plugins.jte)
    alias(libs.plugins.buildtools.native)
    id("io.micronaut.build.internal.java-base")
}

dependencies {
    annotationProcessor(platform(mn.micronaut.core.bom))
    annotationProcessor(mn.micronaut.inject.java)
    implementation(platform(mn.micronaut.core.bom))
    implementation(projects.micronautViewsJte)
    implementation(projects.micronautViewsFieldset)
    implementation(libs.jakarta.validation.api)
    testImplementation(projects.micronautViewsJte)
    jteGenerate(libs.managed.jte.native.resources)
}
jte {
    sourceDirectory = file("src/test/jte").toPath()
    generate()
    jteExtension("gg.jte.nativeimage.NativeResourcesExtension")
}
