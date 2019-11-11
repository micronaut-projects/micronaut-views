package io.micronaut.views.thymeleaf

import io.micronaut.context.ApplicationContext
import io.micronaut.context.MessageSource
import spock.lang.Specification

class SampleMessageSourceSpec extends Specification {

    void "test sample messages bundled"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()

        when:
        SampleMessageSource sampleMessageSource = ctx.getBean(SampleMessageSource)

        then:
        "Sample Title" == sampleMessageSource.getMessage("sample.title", MessageSource.MessageContext.DEFAULT).orElse("null")
    }

    void "test sample messages spanish locale"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()

        when:
        SampleMessageSource sampleMessageSource = ctx.getBean(SampleMessageSource)

        then:
        "Título de muestra" == sampleMessageSource.getMessage("sample.title", MessageSource.MessageContext.of(new Locale("es", "ES"))).orElse("null")
    }

    void "test interpolate"() {
        given:
        ApplicationContext ctx = ApplicationContext.run()

        when:
        SampleMessageSource sampleMessageSource = ctx.getBean(SampleMessageSource)
        String message = sampleMessageSource.getMessage("sample.template", MessageSource.MessageContext.DEFAULT).orElse("null")
        String result = sampleMessageSource.interpolate(message, MessageSource.MessageContext.of(["0":"firstName", "1":"lastName"]))

        then:
        result == "This is a message with first substitution firstName and then second substitution lastName."
    }
}
