plugins {
    `java-library`
    id("io.micronaut.build.internal.views-tests")
}
dependencies {
    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(mnLogging.logback.classic)
    testImplementation(mn.micronaut.http.client)
    testAnnotationProcessor(mnSerde.micronaut.serde.processor)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(projects.micronautViewsThymeleaf)
    testImplementation(mnSecurity.micronaut.security.csrf)
    testImplementation(mnSecurity.micronaut.security.session)
}
tasks.withType<Test> {
    useJUnitPlatform()
}
