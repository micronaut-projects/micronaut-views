package io.micronaut.views.thymeleaf;

import io.micronaut.context.MessageSource;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.util.Validate;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Resolves messages with the Micronaut MessageSource. Allows Thymeleaf templates to use
 * micronaut message properties.
 *
 * @author Nirav Assar
 */
@Singleton
public class MicronautThymeMessageResolver extends AbstractMessageResolver {

    private final StandardMessageResolver standardMessageResolver;
    private MessageSource messageSource;

    /**
     * Constructor which will initialize teh thymeleaf standard message resolver, and also the message source for
     * Micronaut.
     *
     * @param messageSource Micronaut message source
     */
    public MicronautThymeMessageResolver(MessageSource messageSource) {
        super();
        this.standardMessageResolver = new StandardMessageResolver();
        this.messageSource = messageSource;
    }

    @Override
    public final String resolveMessage(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {

        Validate.notNull(key, "Message key cannot be null");

        if (messageParameters.length > 0) {
            Map<String, Object> variables = translateMessagesToMap(messageParameters);
            Optional<String> template = this.messageSource.getMessage(key, MessageSource.MessageContext.DEFAULT);
            if (template.isPresent()) {
                return this.messageSource.interpolate(template.get(), MessageSource.MessageContext.of(variables));
            }
        }

        Optional<String> value = this.messageSource.getMessage(key, MessageSource.MessageContext.DEFAULT);
        return value.orElse(null);
    }

    @Override
    public String createAbsentMessageRepresentation(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        return this.standardMessageResolver.createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }

    /**
     * Turn the object of message parameters into a map so the message source can interpolate the template message.
     *
     * @param messageParameters variables to fill the template
     * @return map of messageParameter with the key being the index
     */
    private Map<String, Object> translateMessagesToMap(Object[] messageParameters) {
        int i;
        Map<String, Object> messageMap = new HashMap<>();
        for (i = 0; i < messageParameters.length; i++) {
            String value = (String) messageParameters[i];
            messageMap.put(String.valueOf(i), value);
        }
        return messageMap;
    }
}
