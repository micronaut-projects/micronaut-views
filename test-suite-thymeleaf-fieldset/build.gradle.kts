plugins {
    id("io.micronaut.application")
}
version = "0.1"
group = "com.example"
repositories {
    mavenCentral()
}
dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")

    //annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    //implementation("io.micronaut.serde:micronaut-serde-jackson")

    implementation("io.micronaut:micronaut-jackson-databind")

    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")

    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    implementation("io.micronaut.validation:micronaut-validation")
    
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("com.h2database:h2")

    implementation(projects.micronautViewsThymeleaf)
    implementation(projects.micronautViewsFieldset)
    implementation(projects.micronautViewsFieldsetTck)

    implementation("io.micronaut.reactor:micronaut-reactor")

    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("org.springframework.security:spring-security-crypto:5.7.2")
    runtimeOnly("org.slf4j:jcl-over-slf4j")

    testImplementation(libs.junit.jupiter.api)
    testImplementation(mnTest.micronaut.test.junit5)
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.platform.engine)
}
application {
    mainClass.set("com.projectcheckins.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    version.set("4.1.5")
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
}
