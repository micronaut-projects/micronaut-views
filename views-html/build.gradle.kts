import io.micronaut.build.TestFramework

plugins {
    id("io.micronaut.build.internal.views-module")
}
dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    implementation(mnValidation.micronaut.validation)
    annotationProcessor(mnSerde.micronaut.serde.processor)
    implementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mnTest.junit.jupiter.params)
    testAnnotationProcessor(mn.micronaut.inject.java)
}
micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
    testFramework = TestFramework.JUNIT6
}
