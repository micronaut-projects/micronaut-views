package io.micronaut.views

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.ApplicationConfiguration
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.views.model.ConfigViewModelProcessor
import io.micronaut.views.model.FruitsController
import io.micronaut.views.model.ViewModelProcessor
import jakarta.inject.Inject
import jakarta.inject.Singleton
import spock.lang.Specification

@Property(name = "spec.name", value = "ModelAndViewSpec")
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.security.views-model-decorator.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.application.name", value = "test")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@MicronautTest
class ModelAndViewSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient

    @Inject
    BeanContext beanContext

    void 'a view model can be any object'() {
        when:
        BlockingHttpClient client = httpClient.toBlocking()

        then:
        beanContext.containsBean(FruitsController.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/")
        HttpResponse<String> response = client.exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        html.contains("<h1>fruit: apple</h1>")

        and:
        html.contains("<h1>color: red</h1>")
    }

    void 'returning a null model causes a 404'() {
        when:
        BlockingHttpClient client = httpClient.toBlocking()

        then:
        beanContext.containsBean(FruitsController.class)

        when:
        client.exchange(HttpRequest.GET("/null"), String.class)

        then:
        HttpClientResponseException thrown = thrown()
        thrown.getResponse().status() == HttpStatus.NOT_FOUND
    }

    void 'a view model can be a Map'() {
        when:
        BlockingHttpClient client = httpClient.toBlocking()

        then:
        beanContext.containsBean(FruitsController.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/map")
        HttpResponse<String> response = client.exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        html.contains("<h1>fruit: orange</h1>")

        and:
        html.contains("<h1>color: orange</h1>")
    }

    void 'models can be dynamically enhanced'() {
        when:
        BlockingHttpClient client = httpClient.toBlocking()

        then:
        beanContext.containsBean(FruitsController.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/processor")
        HttpResponse<String> response = client.exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        beanContext.containsBean(ConfigViewModelProcessor.class)

        and:
        html.contains("<h1>config: test</h1>")
    }

    void 'view model processors work with controllers returning POJOs'() {
        when:
        BlockingHttpClient client = httpClient.toBlocking()

        then:
        beanContext.containsBean(FruitsController.class)

        when:
        HttpRequest<?> request = HttpRequest.GET("/pojo-processor")
        HttpResponse<String> response = client.exchange(request, String.class)

        then:
        response.status() == HttpStatus.OK

        when:
        String html = response.body()

        then:
        html != null

        and:
        beanContext.containsBean(ConfigViewModelProcessor.class)

        and:
        html.contains("<h1>config: test</h1>")
    }

    @Requires(property = "spec.name", value = "ModelAndViewSpec")
    @Controller
    static class ViewModelProcessorController {

        @View("fruits-processor")
        @Get("/pojo-processor")
        Fruit pojoProcessor() {
            return new Fruit("orange", "orange")
        }
    }

    @Introspected
    static abstract class AbstractView {

        String applicationName

        void setApplicationName(String applicationName) {
            this.applicationName = applicationName
        }

        String getApplicationName() {
            return applicationName
        }
    }

    @Introspected
    static class Fruit extends AbstractView {

        String name
        String color

        Fruit(String name, String color) {
            this.name = name
            this.color = color
        }
    }

    @Requires(property = "spec.name", value = "ModelAndViewSpec")
    @Singleton
    static class CustomViewModelProcessor implements ViewModelProcessor<AbstractView, HttpRequest<?>> {

        private final ApplicationConfiguration config

        CustomViewModelProcessor(ApplicationConfiguration environment) {
            this.config = environment
        }

        @Override
        void process(@NonNull HttpRequest<?> request,
                            @NonNull ModelAndView<AbstractView> modelAndView) {
            modelAndView.getModel()
                    .ifPresent(model -> {
                        if (config.getName().isPresent()) {
                            model.setApplicationName(config.getName().get())
                        }
                    })
        }
    }
}
