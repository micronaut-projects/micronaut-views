plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    implementation(mnValidation.micronaut.validation)
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnValidation.micronaut.validation)

    compileOnly(projects.micronautViewsCore)
    compileOnly(mnData.micronaut.data.model)
    testImplementation(mnData.micronaut.data.model)

    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testRuntimeOnly(mnLogging.logback.classic)
}
micronautBuild {
    binaryCompatibility {
        enabled = false
    }
}
