package io.micronaut.views.fields.controllers.read;

import io.micronaut.core.annotation.NonNull;
import java.util.Locale;

public interface ReadHtmlRenderer {
    @NonNull
    String render(@NonNull Locale locale,
                  @NonNull Object item);

}
