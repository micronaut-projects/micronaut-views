plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    api(projects.micronautViewsCore)
    compileOnly(mn.micronaut.http)
    testRuntimeOnly(mnLogging.logback.classic)
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
}
micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}