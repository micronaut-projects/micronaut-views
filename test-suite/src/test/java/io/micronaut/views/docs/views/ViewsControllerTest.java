package io.micronaut.views.docs.views;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "spec.name", value = "ViewsControllerTest")
@MicronautTest
class ViewsControllerTest {

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void aViewCanBeRenderedFromAMapAPojoOrAModelAndView() {
        BlockingHttpClient client = httpClient.toBlocking();
        for (String path : new String[] {"/views", "/views/pojo", "/views/modelAndView"}) {
            String html = client.retrieve(HttpRequest.GET(path), String.class);
            assertTrue(html.contains("<h1>username: <span>sdelamo</span></h1>"), path);
        }
    }
}
