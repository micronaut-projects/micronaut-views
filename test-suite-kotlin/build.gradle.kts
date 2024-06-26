plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    id("io.micronaut.build.internal.views-tests")
}

dependencies {
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnValidation.micronaut.validation)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)

    testImplementation(libs.kotlin.stdlib.jdk8)
    kaptTest(mn.micronaut.inject.java)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)

    testImplementation(projects.micronautViewsHtmx)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(projects.micronautViewsSoy)
    testImplementation(libs.kotlinx.coroutines.core)
    testImplementation(projects.micronautViewsVelocity)
    testImplementation(projects.micronautViewsHandlebars)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(mnLogging.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
