plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    implementation(mnValidation.micronaut.validation)
    compileOnly(mn.micronaut.http)
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnValidation.micronaut.validation)

    compileOnly(projects.micronautViewsCore)
    testImplementation(mnData.micronaut.data.model)
    testImplementation(mn.micronaut.http)
    testImplementation(mn.micronaut.http.server.netty)
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
