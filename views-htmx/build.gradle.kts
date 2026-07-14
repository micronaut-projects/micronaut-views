plugins {
    id("io.micronaut.build.internal.views-module")
}
dependencies {
    api(projects.micronautViewsCore)
    compileOnly(mn.micronaut.http)
    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mnTest.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testRuntimeOnly(mnLogging.logback.classic)
    testAnnotationProcessor(mnSerde.micronaut.serde.processor)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautViewsThymeleaf)
}
