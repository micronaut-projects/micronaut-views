import io.micronaut.build.TestFramework
plugins {
    id("io.micronaut.build.internal.views-module")
}
micronautBuild {
    binaryCompatibility.enabledAfter("6.2.0")
}
dependencies {
    api(projects.micronautViewsCore)
    api(libs.managed.jinjava)
    constraints {
        implementation(libs.commons.lang3) {
            because("GHSA-j288-q9x7-2f5v")
        }
        implementation(libs.jackson.core) {
            because("GHSA-72hv-8253-57qq")
        }
        implementation(libs.jackson.databind) {
            because("GHSA-5jmj-h7xm-6q6v")
        }
    }
    testAnnotationProcessor(mn.micronaut.inject.java)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    // the generation memory test starts a tiny application through micronaut-dev, compiling it with the processor on the test classpath
    testImplementation(mn.micronaut.dev.tck)
    testImplementation(mn.micronaut.inject.java)
}
micronautBuild {
    testFramework = TestFramework.JUNIT6
}
