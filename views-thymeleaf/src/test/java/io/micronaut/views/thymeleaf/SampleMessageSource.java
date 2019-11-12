package io.micronaut.views.thymeleaf;

import io.micronaut.context.i18n.ResourceBundleMessageSource;

import javax.inject.Singleton;

@Singleton
public class SampleMessageSource extends ResourceBundleMessageSource {

    public SampleMessageSource() {
        super("i18n.sample");
    }
}
