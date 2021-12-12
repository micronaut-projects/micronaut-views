/*
 * Copyright 2017-2019 original authors
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
package views.model.security;

import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.model.security.BooksController;
import io.micronaut.views.model.security.MockAuthenticationProvider;
import io.micronaut.views.model.security.SecurityViewModelProcessor;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class SecurityViewModelProcessorTest {

    @Test
    void ifMicronautSecurityViewsModelDecoratorEnabledTrueSecurityViewsModelDecoratorBeanExists() {
        //given:
        ApplicationContext applicationContext = ApplicationContext.run(CollectionUtils.mapOf(
                "micronaut.views.soy.enabled", StringUtils.FALSE,
                        "micronaut.security.views-model-decorator.enabled", StringUtils.FALSE));

        //expect:
        assertFalse(applicationContext.containsBean(SecurityViewModelProcessor.class));

        //cleanup:
        applicationContext.close();
    }

    @Test
    void byDefaultSecurityViewsModelDecoratorBeanExists() {
        //given:
        ApplicationContext applicationContext = ApplicationContext.run(Collections.singletonMap(
                "micronaut.views.soy.enabled", StringUtils.FALSE)
        );

        //expect:
        assertTrue(applicationContext.containsBean(SecurityViewModelProcessor.class));

        //cleanup:
        applicationContext.close();
    }

    @Test
    void aCustomSecurityPropertyNameCanBeInjectedToTheModel() {
        //given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, CollectionUtils.mapOf(
                "spec.name", "SecurityViewModelProcessorSpec",
                "micronaut.views.soy.enabled", StringUtils.FALSE,
                "micronaut.security.views-model-decorator.security-key", "securitycustom"));
        HttpClient httpClient = HttpClient.create(embeddedServer.getURL());

        //expect:
        assertTrue(embeddedServer.getApplicationContext().containsBean(BooksController.class));

        //and:
        assertTrue(embeddedServer.getApplicationContext().containsBean(MockAuthenticationProvider.class));

        //and:
        assertTrue(embeddedServer.getApplicationContext().containsBean(SecurityViewModelProcessor.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/").basicAuth("john", "secret");
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertFalse(html.contains("User: john"));

        //and:
        assertTrue(html.contains("Custom: john"));

        //cleanup:
        httpClient.close();

        //and:
        embeddedServer.close();
    }

    @Test
    void securityPropertyIsInjectedToTheModel() {
        //given:
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, CollectionUtils.mapOf(
                "spec.name", "SecurityViewModelProcessorSpec",
                "micronaut.views.soy.enabled", StringUtils.FALSE));

        HttpClient httpClient = HttpClient.create(embeddedServer.getURL());

        //expect:
        assertTrue(embeddedServer.getApplicationContext().containsBean(BooksController.class));

        //and:
        assertTrue(embeddedServer.getApplicationContext().containsBean(MockAuthenticationProvider.class));

        //and:
        assertTrue(embeddedServer.getApplicationContext().containsBean(SecurityViewModelProcessor.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/").basicAuth("john", "secret");
        HttpResponse<String> response = httpClient.toBlocking().exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);
        assertTrue(html.contains("User: john email: john@email.com"));

        //and:
        assertTrue(html.contains("Developing Microservices"));

        //cleanup:
        httpClient.close();

        //and:
        embeddedServer.close();
    }
}
