plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)

    api(projects.micronautViewsCore)
    api(libs.managed.soy) {
        exclude(group = "org.json", module = "json")
    }
    implementation(libs.org.json)

    compileOnly(mn.micronaut.management)
    compileOnly(mnValidation.micronaut.validation)
    compileOnly(mn.micronaut.http)

    testCompileOnly(mn.micronaut.inject.groovy)
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testAnnotationProcessor(mn.micronaut.inject.java)

    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.management)
    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mn.snakeyaml)
}