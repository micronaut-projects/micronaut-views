plugins {
    groovy
    id("io.micronaut.build.internal.views-tests")
}

dependencies {
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testCompileOnly(mn.micronaut.inject.groovy)

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
    testImplementation(projects.micronautViewsVelocity)
    testImplementation(projects.micronautViewsHandlebars)
    testRuntimeOnly(mnLogging.logback.classic)
}

tasks.withType<Test> {
    useJUnitPlatform()
}