package io.micronaut.views.thymeleaf

import io.micronaut.context.i18n.ResourceBundleMessageSource

import jakarta.inject.Singleton

@Singleton
class SampleMessageSource extends ResourceBundleMessageSource {

    SampleMessageSource() {
        super("i18n/sample");
    }
}
