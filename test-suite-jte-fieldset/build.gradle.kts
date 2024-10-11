plugins {
    id("io.micronaut.build.internal.views-fieldset-tck")
    alias(libs.plugins.jte)
}

dependencies {
    annotationProcessor(platform(mn.micronaut.core.bom))
    annotationProcessor(mn.micronaut.inject.java)
    implementation(platform(mn.micronaut.core.bom))
    implementation(projects.micronautViewsJte)
    implementation(projects.micronautViewsFieldset)
    testImplementation(projects.micronautViewsJte)
}

jte {
    sourceDirectory = file("src/test/jte").toPath()
    generate()
}
