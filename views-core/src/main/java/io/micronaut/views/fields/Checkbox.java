package io.micronaut.views.fields;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

public class Checkbox {

    @NonNull
    private final String name;

    @NonNull
    private final String value;

    private final boolean checked;

    @Nullable
    private final String id;

    @Nullable
    private final Message label;

    public Checkbox(@NonNull String name,
                    String value,
                    boolean checked,
                    @Nullable String id,
                    @Nullable Message label) {
        this.name = name;
        this.value = value;
        this.checked = checked;
        this.id = id;
        this.label = label;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    public boolean isChecked() {
        return checked;
    }

    @Nullable
    public String getId() {
        return id;
    }

    @Nullable
    public Message getLabel() {
        return label;
    }
}
