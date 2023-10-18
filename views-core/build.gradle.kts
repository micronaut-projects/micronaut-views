plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)

    compileOnly(mnSecurity.micronaut.security)
    compileOnly(mn.micronaut.management)
    compileOnly(mnValidation.micronaut.validation)

    implementation(mn.reactor)

    testRuntimeOnly(mnLogging.logback.classic)

    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.inject.java)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.management)
    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mn.snakeyaml)
}
