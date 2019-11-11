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
        // the model for the template
        Context context = new Context();
        context.setVariable("firstName", "SampleFirstName");
        context.setVariable("lastName", "SampleLastName");
        String content = templateEngine.process("thymeleaf/sample", context)

        then:
        content.contains("Sample Title")
        content.contains("Sample body")
        content.contains("SampleFirstName")
        content.contains("SampleLastName")
    }

    void "test template engine uses thyme standard resolver"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        String content = templateEngine.process("thymeleaf/sampleStandard", new Context())

        then:
        content.contains("Standard Title")
    }

    void "test template engine uses standard resolver"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        String content = templateEngine.process("thymeleaf/sample_error", new Context())

        then:
        content.contains("nothing_en_US")
    }
}
