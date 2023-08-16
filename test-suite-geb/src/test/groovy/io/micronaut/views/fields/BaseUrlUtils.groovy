package io.micronaut.views.fields

import groovy.transform.CompileStatic
import io.micronaut.runtime.server.EmbeddedServer
import org.testcontainers.Testcontainers

@CompileStatic
class BaseUrlUtils {
    static String getBaseUrl(EmbeddedServer embeddedServer) {
        "http://${TestContainersUtils.host}:${getPort(embeddedServer)}"
    }

    private static String getPort(EmbeddedServer embeddedServer) {
        if (TestContainersUtils.isGebUsingTestContainers()) {
            Testcontainers.exposeHostPorts(embeddedServer.port)
        }
        embeddedServer.port
    }
}

