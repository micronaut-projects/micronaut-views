plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    api(projects.micronautViewsCore)
    api(libs.managed.jstachio)
    api(mn.micronaut.http)

    testAnnotationProcessor(mn.micronaut.inject.java)
    testAnnotationProcessor(libs.managed.jstachio.apt)
    
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
}

tasks.withType<JavaCompile> {
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-Ajstache.resourcesPath=src/test/resources")
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}

