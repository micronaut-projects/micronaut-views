package io.micronaut.views.jstachio;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.Order;
import io.micronaut.core.io.Writable;
import io.micronaut.core.order.Ordered;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.body.MessageBodyWriter;
import io.micronaut.http.codec.CodecException;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.OutputStream;

@Requires(property = "spec.name", value = "HtmlControllerSpec")
@Order(Ordered.LOWEST_PRECEDENCE)
@Produces(MediaType.TEXT_HTML)
@Singleton
public class PersonMessageBodyWriter<T> implements MessageBodyWriter<T> {

    @Override
    public boolean isWriteable(@NonNull Argument<T> type, @Nullable MediaType mediaType) {
        return type.getType().equals(Person.class);
    }

    @Override
    public void writeTo(@NonNull Argument<T> type,
                        @NonNull MediaType mediaType,
                        T object,
                        @NonNull MutableHeaders outgoingHeaders,
                        @NonNull OutputStream outputStream) throws CodecException {
        if (object instanceof Person person) {
            Writable writable = out -> {
                out.write("<!DOCTYPE html><html><head></head><body><h1>" + person.firstName() + "</h1></body></html>");
            };
            try {
                writable.writeTo(outputStream);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
