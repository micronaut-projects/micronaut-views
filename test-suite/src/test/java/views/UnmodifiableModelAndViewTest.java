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
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.micronaut.views.model.ConfigViewModelProcessor;
import io.micronaut.views.model.FruitsController;
import io.micronaut.views.model.UnmodifiableFruitsController;
import io.micronaut.views.model.ViewModelProcessor;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Issue: https://github.com/micronaut-projects/micronaut-views/issues/336
class UnmodifiableModelAndViewTest {

    @Test
    void anUnmodifiableViewModelReportsErrorButDoesNotCrash() {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, CollectionUtils.mapOf(
                "spec.name", "UnmodifiableModelAndViewSpec",
                "micronaut.views.soy.enabled", StringUtils.FALSE));
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL());

        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //expect:
        assertTrue(embeddedServer.getApplicationContext().containsBean(UnmodifiableFruitsController.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/unmodifiable").basicAuth("john", "secret");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertTrue(html.contains("<blink>Security was added</blink>"));

        //and:
        assertTrue(html.contains("<h1>fruit: plum</h1>"));

        //and:
        assertTrue(html.contains("<h1>color: plum</h1>"));

        //cleanup:
        httpClient.close();

        //and:
        embeddedServer.close();
    }

    @Test
    void aModifiableViewModelStillAddsSecurity() {
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, CollectionUtils.mapOf(
                "spec.name", "UnmodifiableModelAndViewSpec",
                "micronaut.views.soy.enabled", StringUtils.FALSE));
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL());

        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //expect:
        assertTrue(embeddedServer.getApplicationContext().containsBean(UnmodifiableFruitsController.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/modifiable").basicAuth("john", "secret");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertTrue(html.contains("<blink>Security was added</blink>"));

        //and:
        assertTrue(html.contains("<h1>fruit: plum</h1>"));

        //and:
        assertTrue(html.contains("<h1>color: plum</h1>"));

        //cleanup:
        httpClient.close();

        //and:
        embeddedServer.close();
    }
}
