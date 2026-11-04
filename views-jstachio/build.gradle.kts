plugins {
    id("io.micronaut.build.internal.views-module")
}

dependencies {
    api(projects.micronautViewsCore)
    api(libs.managed.jstachio)
    compileOnly(mn.micronaut.http)
    testAnnotationProcessor(mn.micronaut.inject.java)
    testAnnotationProcessor(libs.managed.jstachio.apt)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)

    // the launcher test starts a tiny application through micronaut-dev, compiling it with the processors on the test classpath
    testImplementation(mn.micronaut.dev.tck)
    testImplementation(mn.micronaut.inject.java)
    testImplementation(libs.managed.jstachio.apt)
    testImplementation(mnTest.junit.jupiter.api)
    testRuntimeOnly(mnTest.junit.jupiter.engine)
}

tasks.withType<JavaCompile> {
    val compilerArgs = options.compilerArgs
    compilerArgs.add("-Ajstache.resourcesPath=src/test/resources")
}
