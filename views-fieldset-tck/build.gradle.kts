plugins {
    id("io.micronaut.build.internal.views-module")
}
dependencies {
    api(mnTest.micronaut.test.junit5)
    api(projects.micronautViewsFieldset)
    api(projects.micronautViewsCore)
    api(mn.micronaut.http)
    annotationProcessor(mnValidation.micronaut.validation.processor)
    api(mnValidation.micronaut.validation)
}
micronautBuild {
    binaryCompatibility {
        enabled = false
    }
}