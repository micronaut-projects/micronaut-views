/*
 * Copyright 2017-2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package views;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.views.model.RedirectFruitsController;
import io.micronaut.views.model.UnmodifiableFruitsController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Issue: https://github.com/micronaut-projects/micronaut-views/issues/360
class RedirectModelAndViewTest {

    @Test
    void aRedirectingResponseIsPassedThrough() {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, CollectionUtils.mapOf(
                "spec.name", "RedirectModelAndViewSpec",
                "micronaut.views.soy.enabled", StringUtils.FALSE));

        DefaultHttpClientConfiguration configuration = new DefaultHttpClientConfiguration();
        configuration.setFollowRedirects(false);
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL(), configuration);

        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //expect:
        assertTrue(embeddedServer.getApplicationContext().containsBean(RedirectFruitsController.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/redirect").basicAuth("john", "secret");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.status());

        //when:
        String html = response.body();

        //then:
        assertNull(html);

        //cleanup:
        httpClient.close();

        //and:
        embeddedServer.close();
    }

}
