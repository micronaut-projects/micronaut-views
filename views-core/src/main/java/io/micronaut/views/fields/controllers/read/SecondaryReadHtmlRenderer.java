/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.fields.controllers.read;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Secondary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.BeanWrapper;
import io.micronaut.core.util.StringUtils;
import io.micronaut.views.fields.Message;
import jakarta.inject.Singleton;
import java.util.Locale;

@Secondary
@Singleton
public class SecondaryReadHtmlRenderer implements ReadHtmlRenderer {
    protected final MessageSource messageSource;

    public SecondaryReadHtmlRenderer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @Override
    @NonNull
    public String render(@NonNull Locale locale,
                  @NonNull Object item) {
        return "<!DOCTYPE html><html><body>" + renderItem(locale, item) + "</body></html>";
    }

    @NonNull
    protected String renderItem(@NonNull Locale locale,
                              @NonNull Object item) {
        BeanWrapper<?> beanWrapper = BeanWrapper.getWrapper(item);
        return renderItem(locale, beanWrapper);
    }

    @NonNull
    protected <T> String renderItem(@NonNull Locale locale,
                              BeanWrapper<T> beanWrapper) {
        StringBuilder html = new StringBuilder("<table><tbody>");
        for (BeanProperty<T, Object> beanProperty : beanWrapper.getIntrospection().getBeanProperties()) {
            html.append(renderRow(beanWrapper, beanProperty, locale));
        }
        html.append("</tbody></table>");
        return html.toString();
    }

    @NonNull
    protected <T> String renderRow(@NonNull BeanWrapper<T> beanWrapper,
                                 @NonNull BeanProperty<T, Object> beanProperty,
                                 @NonNull Locale locale) {
        return "<tr>" +
           renderHeaderCell(beanProperty, locale) +
            renderCell(beanWrapper, beanProperty) +
            "</tr>";
    }

    @NonNull
    protected <T> String renderHeaderCell(@NonNull BeanProperty<T, Object> beanProperty,
                                          @NonNull Locale locale) {
        Message message = labelForBeanProperty(beanProperty);
        return "<th>" +
            messageSource.getMessage(message.getCode(), message.getDefaultMessage(), locale) +
            "</th>";
    }

    @NonNull
    protected <T> String renderCell(@NonNull BeanWrapper<T> beanWrapper,
                                    @NonNull BeanProperty<T, Object> beanProperty) {
        Object value = valueForBeanProperty(beanWrapper, beanProperty);
        return "<td>" +
            (value != null ? value : "") +
            "</td>";
    }

    protected Message labelForBeanProperty(BeanProperty<?, ?> beanProperty) {
        return Message.of(beanProperty.getDeclaringBean().getBeanType().getSimpleName().toLowerCase() + "." + beanProperty.getName(),
            StringUtils.capitalize(beanProperty.getName().replaceAll("(.)([A-Z])", "$1 $2")));
    }

    protected  <T> Object valueForBeanProperty(BeanWrapper<T> beanWrapper, BeanProperty<T, Object> beanProperty) {
        return beanProperty.get(beanWrapper.getBean());
    }
}
