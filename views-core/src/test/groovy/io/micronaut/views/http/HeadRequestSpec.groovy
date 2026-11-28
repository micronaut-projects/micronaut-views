package io.micronaut.views.http

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.Writable
import io.micronaut.http.HttpAttributes
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.ModelAndView
import io.micronaut.views.View
import io.micronaut.views.ViewsRenderer
import jakarta.inject.Singleton
import spock.lang.Specification

import java.util.concurrent.CopyOnWriteArrayList

class HeadRequestSpec extends Specification {

    void "a HEAD request for a view is answered as the GET is, with the model the route returned"() {
        given:
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer, ['spec.name': 'HeadRequestSpec', 'micronaut.security.enabled': false])
        BlockingHttpClient client = server.applicationContext.createBean(HttpClient, server.URL).toBlocking()
        GreetingRenderer renderer = server.applicationContext.getBean(GreetingRenderer)

        when:
        HttpResponse<String> got = client.exchange(HttpRequest.GET('/greeting'), String)

        then:
        got.status() == HttpStatus.OK
        got.body() == 'Hello Ada'

        when: "the server has taken the body off the response of a HEAD request by the time a view is resolved"
        HttpResponse<String> head = client.exchange(HttpRequest.HEAD('/greeting'), String)

        then: "the view is not rendered without its model"
        head.status() == HttpStatus.OK
        head.contentType.map { it.name }.orElse(null) == got.contentType.map { it.name }.orElse(null)
        !head.body.present
        !renderer.models.contains(null)

        when: "the route answers later"
        HttpResponse<String> later = client.exchange(HttpRequest.HEAD('/greeting/later'), String)

        then:
        later.status() == HttpStatus.OK
        !renderer.models.contains(null)

        cleanup:
        client.close()
        server.close()
    }

    void "the view of a HEAD response is resolved with what the route returned, which the server keeps in an attribute"() {
        given:
        ApplicationContext context = ApplicationContext.run(['spec.name': 'HeadRequestSpec', 'micronaut.security.enabled': false])
        ModelAndViewResponseBodySwapper swapper = context.getBean(ModelAndViewResponseBodySwapper)
        def route = context.getBeanDefinition(GreetingController).findMethod('greeting').get()

        when: "the response of a HEAD request, as a filter finds it: no body, and what the route returned beside it"
        MutableHttpResponse<Object> response = HttpResponse.ok()
        response.setAttribute(HttpAttributes.ROUTE_MATCH, route)
        response.setAttribute(HttpAttributes.HEAD_BODY, [name: 'Ada'])
        ModelAndView<?> head = swapper.swap(HttpRequest.HEAD('/greeting'), response).get().body()

        then:
        head.view.get() == 'greeting'
        head.model.get() == [name: 'Ada']

        when: "the same response of a GET request has no model to give"
        ModelAndView<?> get = swapper.swap(HttpRequest.GET('/greeting'), response).get().body()

        then:
        !get.model.present

        cleanup:
        context.close()
    }

    @Requires(property = 'spec.name', value = 'HeadRequestSpec')
    @Controller
    static class GreetingController {
        @Get('/greeting')
        @View('greeting')
        Map<String, Object> greeting() {
            [name: 'Ada']
        }

        @Get('/greeting/later')
        @View('greeting')
        java.util.concurrent.CompletableFuture<Map<String, Object>> later() {
            java.util.concurrent.CompletableFuture.supplyAsync { [name: 'Ada'] as Map<String, Object> }
        }
    }

    /**
     * A renderer whose view needs its model, as a template that reads a property of it does.
     */
    @Requires(property = 'spec.name', value = 'HeadRequestSpec')
    @Singleton
    static class GreetingRenderer implements ViewsRenderer<Map<String, Object>, HttpRequest<?>> {
        final List<Map<String, Object>> models = new CopyOnWriteArrayList<>()

        @Override
        Writable render(String viewName, Map<String, Object> data, HttpRequest<?> request) {
            models.add(data)
            return { Writer writer -> writer.write('Hello ' + data.name) } as Writable
        }

        @Override
        boolean exists(String viewName) {
            viewName == 'greeting'
        }
    }
}
