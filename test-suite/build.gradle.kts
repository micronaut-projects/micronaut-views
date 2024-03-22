plugins {
    `java-library`
    id("io.micronaut.build.internal.views-tests")
}

dependencies {
    testAnnotationProcessor(mn.micronaut.inject.java)
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)

    testImplementation(mnValidation.micronaut.validation)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testCompileOnly(mn.micronaut.inject.groovy)
    testImplementation(mn.micronaut.management)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testRuntimeOnly(mnLogging.logback.classic)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(projects.micronautViewsHtmx)
    testImplementation(projects.micronautViewsVelocity)
    testImplementation(projects.micronautViewsCore)
    testImplementation(projects.micronautViewsSoy)
    testImplementation(projects.micronautViewsHandlebars)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
