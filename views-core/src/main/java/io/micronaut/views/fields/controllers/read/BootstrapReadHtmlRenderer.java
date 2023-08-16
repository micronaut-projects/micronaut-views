package io.micronaut.views.fields.controllers.read;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.views.fields.Message;
import io.micronaut.views.fields.controllers.BootstrapUtils;
import jakarta.inject.Singleton;

import java.util.Locale;

@Requires(property = "micronaut.resources.html.theme", value = "bootstrap")
@Replaces(SecondaryReadHtmlRenderer.class)
@Secondary
@Singleton
public class BootstrapReadHtmlRenderer extends SecondaryReadHtmlRenderer {
    public BootstrapReadHtmlRenderer(MessageSource messageSource) {
        super(messageSource);
    }

    @Override
    @NonNull
    public String render(@NonNull Locale locale,
                         @NonNull Object item) {
        return BootstrapUtils.render("", renderItem(locale, item));
    }

    @NonNull
    @Override
    protected  <T> String renderItem(@NonNull Locale locale,
                                  BeanWrapper<T> beanWrapper) {
        StringBuilder html = new StringBuilder("<table class=\"table\">");
        for (BeanProperty<T, Object> beanProperty : beanWrapper.getIntrospection().getBeanProperties()) {
            html.append(renderRow(beanWrapper, beanProperty, locale));
        }
        html.append("</tbody></table>");
        return html.toString();
    }

    @NonNull
    protected <T> String renderHeaderCell(@NonNull BeanProperty<T, Object> beanProperty,
                                          @NonNull Locale locale) {
        Message message = labelForBeanProperty(beanProperty);
        return "<th scope=\"row\">" +
            messageSource.getMessage(message.getCode(), message.getDefaultMessage(), locale) +
            "</th>";
    }
}

