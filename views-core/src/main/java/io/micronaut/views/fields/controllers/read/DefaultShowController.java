package io.micronaut.views.fields.controllers.read;

import io.micronaut.context.BeanContext;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Executable;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.LocaleResolver;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.inject.qualifiers.Qualifiers;

import java.util.Optional;

@EachBean(ReadHtmlResource.class)
public class DefaultShowController<E, ID> implements ShowController<ID> {

    private final ReadHtmlResource<E> readHtmlResource;

    private final ReadHtmlRenderer readHtmlRenderer;

    private final ReadService<E, ID> readService;

    private final LocaleResolver<HttpRequest<?>> localeResolver;

    public DefaultShowController(ReadHtmlResource<E> readHtmlResource,
                                 BeanContext beanContext,
                                 ReadHtmlRenderer readHtmlRenderer,
                                 LocaleResolver<HttpRequest<?>> localeResolver) {
        this.readHtmlResource = readHtmlResource;
        this.readHtmlRenderer = readHtmlRenderer;
        this.readService = beanContext.getBean(ReadService.class, Qualifiers.byName(readHtmlResource.getName()));
        this.localeResolver = localeResolver;
    }

    @Override
    @Produces(MediaType.TEXT_HTML)
    @Executable
    @NonNull
    public HttpResponse<?> show(@NonNull HttpRequest<?> request,
                                @NonNull @PathVariable ID id) {
        Optional<E> instanceOptional = readService.findById(id);
        if (instanceOptional.isEmpty()) {
            return HttpResponse.notFound();
        }
        return HttpResponse.ok(readHtmlRenderer.render(localeResolver.resolveOrDefault(request), instanceOptional.get()));
    }
}
