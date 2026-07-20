package io.micronaut.views.jinjava

import com.hubspot.jinjava.Jinjava
import io.micronaut.context.ApplicationContext
import io.micronaut.core.io.Writable
import io.micronaut.core.io.scan.ClassPathResourceLoader
import spock.lang.Specification

class JinjavaViewsRendererConfigurationSpec extends Specification {

    void "binds Jinjava builder settings"() {
        given:
        ApplicationContext context = ApplicationContext.run([
            'micronaut.views.jinja.config.trim-blocks': true,
        ])

        expect:
        context.getBean(Jinjava).globalConfig.trimBlocks

        cleanup:
        context.close()
    }

    void "renders with no request and only loads existing views"() {
        given:
        ApplicationContext context = ApplicationContext.run()
        def renderer = context.getBean(JinjavaViewsRenderer)

        when:
        Writable writable = renderer.render('tim', [username: 'Tim'], null)
        String result = new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }

        then:
        result.contains('username: <span>Tim</span>')
        renderer.exists('tim')
        !renderer.exists('../application.yml')
        !renderer.exists('missing')

        cleanup:
        context.close()
    }

    void "can be disabled"() {
        when:
        ApplicationContext context = ApplicationContext.run(['micronaut.views.jinja.enabled': false])

        then:
        !context.containsBean(JinjavaViewsRenderer)

        cleanup:
        context.close()
    }

    void "requires Jinjava resource locator to be JinjavaResourceLocator"() {
        given:
        ApplicationContext context = ApplicationContext.run()

        when:
        new JinjavaViewsRenderer(
            new Jinjava(),
            context.getBean(ClassPathResourceLoader),
            new JinjavaViewsRendererConfigurationProperties()
        )

        then:
        IllegalStateException e = thrown()
        e.message == 'Jinjava resource locator must be an instance of JinjavaResourceLocator'

        cleanup:
        context.close()
    }
}
