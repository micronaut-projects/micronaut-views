package views

import com.github.jknack.handlebars.Handlebars
import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.core.util.StringUtils
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.views.View
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.handlebars.HandlebarsViewsRenderer
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfiguration
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfigurationProperties
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import javax.inject.Singleton

class MultipleViewRendererSpec extends Specification {
    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, [
            "micronaut.security.enabled": false,
            'spec.name': 'MultipleViewRendererSpec',
            'micronaut.views.soy.enabled': false,
    ]) as EmbeddedServer

    @Shared
    @AutoCleanup
    HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient, embeddedServer.URL)

    @Shared
    BlockingHttpClient client = httpClient.toBlocking()

    void "multiple ViewsRenderer are possible"() {
        when:
        String html = client.retrieve('/stark', String)

        then:
        html.contains('John Snow')

        when:
        html = client.retrieve('/targaryen', String)

        then:
        html.contains('Aegon Targaryen')
    }

    void "views with same name are selected with ViewsRenderer order"() {
        when:
        String html = client.retrieve('/johnsnow', String)

        then:
        html.contains('John Snow')
    }

    @Requires(property = 'spec.name', value = 'MultipleViewRendererSpec')
    @Produces(MediaType.TEXT_HTML)
    @Requires(property = HandlebarsViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = StringUtils.FALSE)
    @Requires(classes = Handlebars.class)
    @Replaces(HandlebarsViewsRenderer.class)
    @Singleton
    static class CustomHandlebarsViewsRenderer extends HandlebarsViewsRenderer {
        CustomHandlebarsViewsRenderer(ViewsConfiguration viewsConfiguration,
                                       ClassPathResourceLoader resourceLoader,
                                       HandlebarsViewsRendererConfiguration handlebarsViewsRendererConfiguration,
                                       Handlebars handlebars) {
            super(viewsConfiguration, resourceLoader, handlebarsViewsRendererConfiguration, handlebars)
        }

        @Override
        int getOrder() {
            HIGHEST_PRECEDENCE + 100
        }
    }

    @Requires(property = 'spec.name', value = 'MultipleViewRendererSpec')
    @Controller
    static class GotController {
        @View('stark')
        @Get('/stark')
        Map<String, Object> stark() {
            new HashMap<String, Object>();
        }

        @View('johnsnow')
        @Get('/johnsnow')
        Map<String, Object> johnsnow() {
            new HashMap<String, Object>();
        }

        @View('targaryen')
        @Get('/targaryen')
        Map<String, Object> targaryen() {
            new HashMap<String, Object>();
        }
    }
}

