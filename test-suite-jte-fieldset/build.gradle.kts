plugins {
    id("io.micronaut.build.internal.views-fieldset-tck")
    id ("io.micronaut.build.internal.java-base")
    alias(libs.plugins.jte)
    alias(libs.plugins.buildtools.native)
}

dependencies {
    annotationProcessor(platform(mn.micronaut.core.bom))
    annotationProcessor(mn.micronaut.inject.java)
    implementation(platform(mn.micronaut.core.bom))
    implementation(projects.micronautViewsJte)
    implementation(projects.micronautViewsFieldset)
    implementation(platform(mnValidation.micronaut.validation.bom))
    implementation(mnValidation.micronaut.validation)
    testImplementation(projects.micronautViewsJte)
    jteGenerate(libs.managed.jte.native.resources)
}
graalvmNative.toolchainDetection = false
jte {
    sourceDirectory = file("src/test/jte").toPath()
    generate()
    jteExtension("gg.jte.nativeimage.NativeResourcesExtension")
}
