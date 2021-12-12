package views;

import com.github.jknack.handlebars.Handlebars;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.views.View;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.handlebars.HandlebarsViewsRenderer;
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfiguration;
import io.micronaut.views.handlebars.HandlebarsViewsRendererConfigurationProperties;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Property(name = "spec.name", value = "MultipleViewRendererSpec")
@Property(name = "micronaut.security.enabled", value = StringUtils.FALSE)
@Property(name = "micronaut.views.soy.enabled", value = StringUtils.FALSE)
@MicronautTest
class MultipleViewRendererTest {
    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void multipleViewsRendererArePossible() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();

        //when:
        String html = client.retrieve("/stark", String.class);

        //then:
        assertTrue(html.contains("John Snow"));

        //when:
        html = client.retrieve("/targaryen", String.class);

        //then:
        assertTrue(html.contains("Aegon Targaryen"));
    }

    @Test
    void viewsWithSameNameAreSelectedWithViewsRendererOrder() {
        //given:
        BlockingHttpClient client = httpClient.toBlocking();
        //when:
        String html = client.retrieve("/johnsnow", String.class);

        //then:
        assertTrue(html.contains("John Snow"));;
    }

    @Requires(property = "spec.name", value = "MultipleViewRendererSpec")
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
            super(viewsConfiguration, resourceLoader, handlebarsViewsRendererConfiguration, handlebars);
        }

        @Override
        public int getOrder() {
            return HIGHEST_PRECEDENCE + 100;
        }
    }

    @Requires(property = "spec.name", value = "MultipleViewRendererSpec")
    @Controller
    static class GotController {
        @View("stark")
        @Get("/stark")
        Map<String, Object> stark() {
            return new HashMap<String, Object>();
        }

        @View("johnsnow")
        @Get("/johnsnow")
        Map<String, Object> johnsnow() {
            return new HashMap<String, Object>();
        }

        @View("targaryen")
        @Get("/targaryen")
        Map<String, Object> targaryen() {
            return new HashMap<String, Object>();
        }
    }
}

