package io.micronaut.views.rocker.docs;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "spec.name", value = "RockerStaticTemplateTest")
@MicronautTest
class RockerStaticTemplateTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void aCompiledRockerTemplateCanBeReturnedAsARockerWritable() {
        String html = httpClient.toBlocking().retrieve("/rocker-static");
        assertTrue(html.contains("<h1>username: <span>sdelamo</span></h1>"));
    }
}
