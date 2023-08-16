plugins {
    id("io.micronaut.build.internal.views-base")
    groovy
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    testRuntimeOnly(mnLogging.logback.classic)
    testCompileOnly(mnValidation.micronaut.validation.processor)
    testImplementation(mnValidation.micronaut.validation)
    testImplementation(mn.micronaut.inject.groovy)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mnTest.micronaut.test.spock)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(projects.micronautViewsCore)
    testImplementation(libs.geb.spock)
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.selenium)
    testImplementation(libs.selenium.remote.driver)
    testImplementation(libs.selenium.api)
    testImplementation(libs.selenium.support)
    testRuntimeOnly(libs.selenium.firefox.driver)

    testCompileOnly(mnData.micronaut.data.processor)
    testImplementation(mnData.micronaut.data.jdbc)
    testImplementation(mnSql.micronaut.jdbc.hikari)
    testImplementation(mnSql.h2)
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("geb.env", "firefox")
    systemProperty("webdriver.gecko.driver", "/Applications/geckodriver")
    //systemProperty("geb.env", System.getProperty("geb.env") ?: "dockerFirefox")
    //systemProperty("webdriver.gecko.driver", System.getProperty("webdriver.gecko.driver"))
}
