plugins {
    groovy
    id("io.micronaut.build.internal.views-tests")
}

dependencies {
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testCompileOnly(mn.micronaut.inject.groovy)
    testCompileOnly(mnSerde.micronaut.serde.processor)
    testCompileOnly(mnValidation.micronaut.validation.processor)

    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mnTest.micronaut.test.spock)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)

    testImplementation(mnSecurity.micronaut.security)
    testImplementation(mnSerde.micronaut.serde.api)
    testImplementation(mnSerde.micronaut.serde.jackson)
    
    testImplementation(libs.groovy.json)
    testImplementation(projects.micronautViewsHtmx)
    testImplementation(projects.micronautViewsSoy)
    testImplementation(projects.micronautViewsTurbo)
    testImplementation(projects.micronautViewsVelocity)
    testImplementation(projects.micronautViewsHandlebars)
    testImplementation(projects.micronautViewsThymeleaf)
    testImplementation(projects.micronautViewsFieldset)
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