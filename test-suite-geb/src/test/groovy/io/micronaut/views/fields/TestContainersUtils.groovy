package io.micronaut.views.fields

final class TestContainersUtils {
    private TestContainersUtils() {
    }

    static boolean isGebUsingTestContainers() {
        System.getProperty("geb.env") == null || System.getProperty("geb.env").contains("docker");
    }

    static String getHost() {
        isGebUsingTestContainers() ? "host.testcontainers.internal" : "localhost";
    }
}
