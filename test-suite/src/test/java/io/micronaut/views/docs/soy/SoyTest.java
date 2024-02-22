package io.micronaut.views.docs.soy;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "soy")
@Property(name = "micronaut.views.velocity.enabled", value = StringUtils.FALSE)
@MicronautTest
class SoyTest {
    @Inject
    @Client("/")
    public HttpClient httpClient;

    @Test
    void invokingSoyRendersSoyTemplateFromAControllerReturningAmap() {
        //when:
        HttpResponse<String> rsp = httpClient.toBlocking().exchange("/soy", String.class);

        //then:
        assertEquals(HttpStatus.OK, rsp.status());

        //when:
        String body = rsp.body();

        //then:
        assertNotNull(body);
        assertTrue(rsp.body().contains("<h1>username: <span>sgammon</span></h1>"));
    }
}
