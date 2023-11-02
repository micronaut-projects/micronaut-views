plugins {
    id("io.micronaut.build.internal.views-fieldset-tck")
}

dependencies {
    testImplementation(projects.micronautViewsThymeleaf)
    testAnnotationProcessor(mnData.micronaut.data.processor)
    testImplementation(mnData.micronaut.data.jdbc)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.micronaut.http.server.netty)
    testAnnotationProcessor(mnSerde.micronaut.serde.processor)
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mnSql.micronaut.jdbc.hikari)
    testImplementation(platform(mnSql.micronaut.sql.bom))
    testRuntimeOnly("com.h2database:h2")
}
