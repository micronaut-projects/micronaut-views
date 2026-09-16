plugins {
    id("io.micronaut.build.internal.kotlin-kapt")
    id("io.micronaut.build.internal.views-tests")
}

dependencies {
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnValidation.micronaut.validation)

    testImplementation(mnTest.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)

    kaptTest(mn.micronaut.inject.java)
    kaptTest(mnValidation.micronaut.validation.processor)
    kaptTest(mnSerde.micronaut.serde.processor)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)

    testImplementation(projects.micronautViewsHtmx)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(projects.micronautViewsSoy)
    testImplementation(projects.micronautViewsTurbo)
    testImplementation(mn.kotlinx.coroutines.core)
    testImplementation(projects.micronautViewsVelocity)
    testImplementation(projects.micronautViewsHandlebars)
    testImplementation(projects.micronautViewsThymeleaf)
    testImplementation(projects.micronautViewsFieldset)

    testRuntimeOnly(mnTest.junit.jupiter.engine)
    testRuntimeOnly(mnLogging.logback.classic)
}

// The fieldset documentation examples render forms with the Thymeleaf fragments of the fieldset test suite
tasks.named<ProcessResources>("processTestResources") {
    from(rootProject.file("test-suite-thymeleaf-fieldset/src/test/resources/views/fieldset")) {
        into("views/fieldset")
        exclude("*.md")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
