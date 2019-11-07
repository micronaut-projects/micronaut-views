package io.micronaut.views.thymeleaf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.MessageSource
import spock.lang.Specification

class SampleMessageSourceSpec extends Specification {

    void "test sample messages are bundled"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()

        when:
        SampleMessageSource sampleMessageSource = ctx.getBean(SampleMessageSource)

        then:
        "Sample Title" == sampleMessageSource.getMessage("sample.title", MessageSource.MessageContext.DEFAULT).orElse("null")
    }
}
