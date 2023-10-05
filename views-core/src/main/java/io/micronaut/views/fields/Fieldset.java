package io.micronaut.views.fields;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;

import java.util.List;

@Introspected
public class Fieldset {

    @NonNull
    private final List<? extends FormElement> fields;

    @NonNull
    private final List<Message> errors;


    public Fieldset(@NonNull List<? extends FormElement> fields,
                    @NonNull List<Message> errors) {
        this.fields = fields;
        this.errors = errors;
    }

    @NonNull
    public List<? extends FormElement> getFields() {
        return fields;
    }

    @NonNull
    public List<Message> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return CollectionUtils.isNotEmpty(errors);
    }
}
