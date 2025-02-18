package io.micronaut.views.react;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

public final class WritableUtils {
    private WritableUtils() {
    }

    @NonNull
    public static Optional<String> writableToString(@NonNull Writable writable) {
        if (writable == null) {
            return Optional.empty();
        }
        try {
            StringWriter stringWriter = new StringWriter();
            writable.writeTo(stringWriter);
            return Optional.of(stringWriter.toString());
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
