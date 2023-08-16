package io.micronaut.views.fields.controllers.createsave;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;
import io.micronaut.views.fields.controllers.HtmlResource;

public interface CreateHtmlResource<C> extends HtmlResource {
    @Override
    default @NonNull String getName() {
        return createClass().getSimpleName().toLowerCase();
    }

    Class<C> createClass();

    default String createPath() {
        return "/" +getName() + "/create";
    }

    default String savePath() {
        return "/" +getName() + "/save";
    }
}
