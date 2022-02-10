/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.thymeleaf;

import io.micronaut.context.MessageSource;
import io.micronaut.context.MessageSource.MessageContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.util.Validate;

import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves messages with the Micronaut MessageSource. Allows Thymeleaf templates to use
 * Micronaut message properties.
 *
 * @author Nirav Assar
 * @since 1.3.0
 */
@Singleton
public class MessageSourceMessageResolver extends AbstractMessageResolver {

    private final StandardMessageResolver standardMessageResolver;
    private final MessageSource messageSource;

    /**
     * Constructor which will initialize the Thymeleaf standard message resolver, and also the message source for
     * Micronaut.
     *
     * @param messageSource Micronaut message source
     */
    public MessageSourceMessageResolver(MessageSource messageSource) {
        this.standardMessageResolver = new StandardMessageResolver();
        this.messageSource = messageSource;
    }

    @Override
    public final String resolveMessage(final ITemplateContext context,
                                       final Class<?> origin,
                                       final String key,
                                       final Object[] messageParameters) {

        Validate.notNull(context.getLocale(), "Locale in context cannot be null");
        Validate.notNull(key, "Message key cannot be null");

        MessageContext messageContext = MessageContext.of(context.getLocale(),  translateMessagesToMap(messageParameters));

        return messageSource.getMessage(key, messageContext)
                .map(template -> messageSource.interpolate(template, messageContext))
                .orElse(null);
    }

    @Override
    public String createAbsentMessageRepresentation(final ITemplateContext context,
                                                    final Class<?> origin,
                                                    final String key,
                                                    final Object[] messageParameters) {

        return standardMessageResolver.createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }

    /**
     * Turn the object of message parameters into a map so the message source can interpolate the template message.
     *
     * @param messageParameters variables to fill the template
     * @return map of messageParameter with the key being the index
     */
    private Map<String, Object> translateMessagesToMap(Object[] messageParameters) {
        if (messageParameters.length > 0) {
            int i;
            Map<String, Object> messageMap = new HashMap<>();
            for (i = 0; i < messageParameters.length; i++) {
                Object val = messageParameters[i];
                if (val != null) {
                    String value = val.toString();
                    messageMap.put(String.valueOf(i), value);
                }
            }
            return messageMap;
        } else {
            return null;
        }
    }
}
