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
package io.micronaut.views;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.micronaut.views.model.ConfigViewModelProcessor;
import io.micronaut.views.model.FruitsController;
import io.micronaut.views.model.ViewModelProcessor;
import io.micronaut.views.turbo.TurboStream;
import io.micronaut.views.turbo.TurboStreamAction;
import io.micronaut.views.turbo.http.TurboMediaType;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

@Property(name = "spec.name", value = "ModelAndViewSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.views-model-decorator.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.application.name", value = "test")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class ModelAndViewTest {
    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    BeanContext beanContext;

    @Test
    void aViewModelCanBeAnyObject() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        ////expect:
        assertTrue(beanContext.containsBean(FruitsController.class));

        ////when:
        HttpRequest<?> request = HttpRequest.GET("/");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertTrue(html.contains("<h1>fruit: apple</h1>"));

        //and:
        assertTrue(html.contains("<h1>color: red</h1>"));
    }

    @Test
    void returningANullModelCausesA404() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //expect:
        assertTrue(beanContext.containsBean(FruitsController.class));

        //when:
        Executable e = () -> client.exchange(HttpRequest.GET("/null"), String.class);

        //then:
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
    }

    @Test
    void aViewModelCanBeAMap() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //expect:
        assertTrue(beanContext.containsBean(FruitsController.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/map");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertTrue(html.contains("<h1>fruit: orange</h1>"));

        //and:
        assertTrue(html.contains("<h1>color: orange</h1>"));
    }

    @Test
    void modelsCanBeDynamicallyEnhanced() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();

        //expect:
        assertTrue(beanContext.containsBean(FruitsController.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/processor");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertTrue(beanContext.containsBean(ConfigViewModelProcessor.class));

        //and:
        assertTrue(html.contains("<h1>config: test</h1>"));
    }

    @Test
    void viewModelProcessorsWorkWithControllersReturningPOJOs() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();

        //expect:
        assertTrue(beanContext.containsBean(FruitsController.class));

        //when:
        HttpRequest<?> request = HttpRequest.GET("/pojo-processor");
        HttpResponse<String> response = client.exchange(request, String.class);

        //then:
        assertEquals(HttpStatus.OK, response.status());

        //when:
        String html = response.body();

        //then:
        assertNotNull(html);

        //and:
        assertTrue(beanContext.containsBean(ConfigViewModelProcessor.class));

        //and:
        assertTrue(html.contains("<h1>config: test</h1>"));

        //when:
        request = HttpRequest.GET("/turboStreamBuilderWithProcessor").accept(TurboMediaType.TURBO_STREAM);
        html = client.retrieve(request, String.class);
        assertTrue(html.contains("<h1>config: test</h1>"));
    }

    @Requires(property = "spec.name", value = "ModelAndViewSpec")
    @Controller
    static class ViewModelProcessorController {
        @View("fruits-processor")
        @Get("/pojo-processor")
        public Fruit pojoProcessor() {
            return new Fruit("orange", "orange");
        }


        @Produces(TurboMediaType.TURBO_STREAM)
        @Get("/turboStreamBuilderWithProrocessor")
        public TurboStream.Builder turboStreamBuilder() {
            return TurboStream.builder()
                    .action(TurboStreamAction.REPLACE)
                    .template("fruits-processor", new Fruit("orange", "orange"));
        }
    }

    @Introspected
    static abstract class AbstractView {
        String applicationName;

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getApplicationName() {
            return applicationName;
        }
    }

    @Introspected
    static class Fruit extends AbstractView {
        String name;
        String color;

        Fruit(String name, String color) {
            this.name = name;
            this.color = color;
        }
    }

    @Requires(property = "spec.name", value = "ModelAndViewSpec")
    @Singleton
    static class CustomViewModelProcessor implements ViewModelProcessor<AbstractView> {
        private final ApplicationConfiguration config;
        CustomViewModelProcessor(ApplicationConfiguration environment) {
            this.config = environment;
        }

        @Override
        public void process(@NonNull HttpRequest<?> request,
                            @NonNull ModelAndView<AbstractView> modelAndView) {
            modelAndView.getModel()
                    .ifPresent(model -> {
                        if (config.getName().isPresent()) {
                            model.setApplicationName(config.getName().get());
                        }
                    });
        }
    }
}
