plugins {
    id("io.micronaut.build.internal.views-module")
}

micronautBuild {
    binaryCompatibility.enabledAfter("6.3.0")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)

    api(projects.micronautViewsReact)
    implementation(mn.micronaut.http)
    implementation(mnReactor.micronaut.reactor)

    compileOnly(mnValidation.micronaut.validation) {
        because("For constraints on config properties")
    }
    compileOnly(mnSecurity.micronaut.security) {
        because("To mark the reload endpoint anonymous where micronaut-security is in use, without depending on it")
    }

    testCompileOnly(mn.micronaut.inject.groovy)
    testAnnotationProcessor(mnValidation.micronaut.validation.processor)
    testAnnotationProcessor(mn.micronaut.inject.java)

    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mnValidation.micronaut.validation)

}
