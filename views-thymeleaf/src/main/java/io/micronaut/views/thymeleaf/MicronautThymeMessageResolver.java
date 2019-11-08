package io.micronaut.views.thymeleaf;

import io.micronaut.context.MessageSource;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.util.Validate;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
public class MicronautThymeMessageResolver extends AbstractMessageResolver {

    private final StandardMessageResolver standardMessageResolver;
    private MessageSource messageSource;

    public MicronautThymeMessageResolver(MessageSource messageSource) {
        super();
        this.standardMessageResolver = new StandardMessageResolver();
        this.messageSource = messageSource;
    }

    public final String resolveMessage(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {

        Validate.notNull(key, "Message key cannot be null");
        Optional<String> value = this.messageSource.getMessage(key, MessageSource.MessageContext.DEFAULT);
        if(value.isPresent()) {
            return value.get();
        }

        return null;
    }

    public String createAbsentMessageRepresentation(
            final ITemplateContext context, final Class<?> origin, final String key, final Object[] messageParameters) {
        return this.standardMessageResolver.createAbsentMessageRepresentation(context, origin, key, messageParameters);
    }
}
