package io.micronaut.views.thymeleaf;

import io.micronaut.context.MessageSource;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.util.Validate;

import javax.inject.Singleton;
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
        Optional<String> value = this.messageSource.getMessage(key, MessageSource.MessageContext.DEFAULT);
        return value.orElse(null);

    }

    @Override
    public String createAbsentMessageRepresentation(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        return this.standardMessageResolver.createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }
}
