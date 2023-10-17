plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    compileOnly(mnValidation.micronaut.validation)
    compileOnly(mnSecurity.micronaut.security)
    compileOnly(mn.micronaut.management)
    compileOnly(mnData.micronaut.data.model)
    implementation(mn.reactor)
    implementation(mn.micronaut.json.core)
    testRuntimeOnly(mnLogging.logback.classic)

    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.inject.java)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.management)
    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mn.snakeyaml)
    testImplementation(mnData.micronaut.data.model)

    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}
