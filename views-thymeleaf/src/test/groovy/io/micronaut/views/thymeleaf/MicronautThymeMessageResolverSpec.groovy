package io.micronaut.views.thymeleaf

import io.micronaut.context.ApplicationContext
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import spock.lang.Specification
class MicronautThymeMessageResolverSpec extends Specification {

    void "test template engine resolves micronaut message bundle"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        String content = templateEngine.process("thymeleaf/sample", new Context())

        then:
        content.contains("Sample Title")
        content.contains("sample body")
    }

    void "test template engine uses standard resolver on error"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        String content = templateEngine.process("thymeleaf/sample_error", new Context())

        then:
        content.contains("nothing_en_US")
    }
}
