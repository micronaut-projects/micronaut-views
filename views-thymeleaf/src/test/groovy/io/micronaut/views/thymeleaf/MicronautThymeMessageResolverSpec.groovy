package io.micronaut.views.thymeleaf

import io.micronaut.context.ApplicationContext
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext
import spock.lang.Specification

class MicronautThymeMessageResolverSpec extends Specification {

    void "test template engine resolves micronaut message bundle"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        // the model for the template
        Context context = new Context()
        context.setVariable("firstName", "SampleFirstName")
        context.setVariable("lastName", "SampleLastName")
        String content = templateEngine.process("thymeleaf/sample", context)

        then:
        content.contains("Sample Title")
        content.contains("Sample body")
        content.contains("SampleFirstName")
        content.contains("SampleLastName")

        cleanup:
        ctx.close()
    }

    void "test template engine resolves micronaut message bundle different locale"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        // the model for the template
        Context context = new Context(new Locale("es", "ES"))
        context.setVariable("firstName", "SampleFirstName")
        context.setVariable("lastName", "SampleLastName")
        String content = templateEngine.process("thymeleaf/sample", context)

        then:
        content.contains("Título de muestra")
        content.contains("Texto de ejemplo")
        content.contains("Este es un mensaje con la primera sustitución SampleFirstName Y luego segunda sustitución SampleLastName.")

        cleanup:
        ctx.close()
    }

    void "test template engine uses thyme standard resolver"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        String content = templateEngine.process("thymeleaf/sampleStandard", new Context())

        then:
        content.contains("Standard Title")

        cleanup:
        ctx.close()
    }

    void "test template engine uses standard resolver"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)

        when:
        String content = templateEngine.process("thymeleaf/sample_error", new Context())

        then:
        content.contains("nothing_en_US")

        cleanup:
        ctx.close()
    }

    void "test message resolver translates non-string parameters"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)
        IContext ictx = new Context(null, ['int1': 123, 'int2': 456])

        when:
        String content = templateEngine.process("thymeleaf/sample_int_params", ictx)

        then:
        content.contains("This is a message with first substitution 123 and then second substitution 456.")

        cleanup:
        ctx.close()
    }
}
