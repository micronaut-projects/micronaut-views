plugins {
    id("io.micronaut.build.internal.views-fieldset-tck")
    alias(libs.plugins.jte)
    alias(libs.plugins.buildtools.native)
}

dependencies {
    annotationProcessor(platform(mn.micronaut.core.bom))
    annotationProcessor(mn.micronaut.inject.java)
    implementation(platform(mn.micronaut.core.bom))
    implementation(projects.micronautViewsJte)
    implementation(projects.micronautViewsFieldset)
    testImplementation(projects.micronautViewsJte)
    jteGenerate(libs.managed.jte.native.resources)
}
graalvmNative.toolchainDetection = true
jte {
    sourceDirectory = file("src/test/jte").toPath()
    generate()
    jteExtension("gg.jte.nativeimage.NativeResourcesExtension")
}
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21
