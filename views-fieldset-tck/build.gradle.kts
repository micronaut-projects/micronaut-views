plugins {
    id("io.micronaut.build.internal.views-module")
}
dependencies {
    implementation(mnTest.micronaut.test.junit5)
    implementation(projects.micronautViewsFieldset)
    implementation(projects.micronautViewsCore)

    annotationProcessor(mnValidation.micronaut.validation.processor)
    implementation(mnValidation.micronaut.validation)
}
micronautBuild {
    binaryCompatibility {
        enabled = false
    }
}