package io.micronaut.views.thymeleaf

import io.micronaut.context.ApplicationContext
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import spock.lang.Specification

class MicronautThymeMessageResolverSpec extends Specification {

    void "test resolver and template engine integrate properly"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()
        TemplateEngine templateEngine = ctx.getBean(TemplateEngine)
        MicronautThymeMessageResolver micronautThymeleafMessageResolver = ctx.getBean(MicronautThymeMessageResolver)
        templateEngine.addMessageResolver(micronautThymeleafMessageResolver)

        when:
        String content = templateEngine.process("thymeleaf/sample", new Context())

        then:
        content.contains("Sample Title")
   }
}
