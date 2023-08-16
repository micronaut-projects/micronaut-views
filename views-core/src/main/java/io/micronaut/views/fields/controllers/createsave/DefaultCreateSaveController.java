package io.micronaut.views.fields.controllers.createsave;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Executable;
import io.micronaut.core.util.LocaleResolver;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Produces;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.json.JsonMapper;
import io.micronaut.json.tree.JsonNode;
import io.micronaut.views.fields.FieldGenerator;
import io.micronaut.views.fields.Fieldset;
import io.micronaut.views.fields.InputField;
import io.micronaut.views.fields.controllers.FieldsetRenderer;
import jakarta.validation.ConstraintViolationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EachBean(CreateHtmlResource.class)
public class DefaultCreateSaveController<T> implements CreateSaveController<T> {
    private final CreateHtmlResource<T> createResource;

    private final SaveService<T> saveService;
    private final FieldGenerator fieldGenerator;
    private final FieldsetRenderer fieldsetRenderer;

    private final LocaleResolver<HttpRequest<?>> localeResolver;

    private final JsonMapper jsonMapper;

    private final Class<T> createType;
    public DefaultCreateSaveController(CreateHtmlResource<T> createResource,
                                       BeanContext beanContext,
                                       FieldGenerator fieldGenerator,
                                       FieldsetRenderer fieldsetRenderer,
                                       LocaleResolver<HttpRequest<?>> localeResolver,
                                       JsonMapper jsonMapper) {
        this.createResource = createResource;
        this.jsonMapper = jsonMapper;
        this.saveService = beanContext.getBean(SaveService.class, Qualifiers.byName(createResource.getName()));
        this.createType = createResource.createClass();
        this.fieldGenerator = fieldGenerator;
        this.fieldsetRenderer = fieldsetRenderer;
        this.localeResolver = localeResolver;
    }

    @Produces(MediaType.TEXT_HTML)
    @Executable
    @Override
    public String create(HttpRequest<?> request) {
        Fieldset fieldList = fieldGenerator.generate(createType);
        return fieldsetRenderer.render(localeResolver.resolveOrDefault(request), createResource.savePath() , fieldList);
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Executable
    @Override
    public HttpResponse<?> save(HttpRequest<?> request, @Body Map<String, Object> form) {
        try {
            Map<String, Object> cleanupForm = emptyStringsToNull(form);
            T obj = jsonMapper.readValueFromTree(JsonNode.from(cleanupForm), createResource.createClass());
            try {
                return HttpResponse.seeOther(saveService.save(obj));
            } catch (ConstraintViolationException e) {
                Fieldset fieldset = fieldGenerator.generate(obj, e);
                return HttpResponse.unprocessableEntity()
                    .contentType(MediaType.TEXT_HTML)
                    .body(fieldsetRenderer.render(localeResolver.resolveOrDefault(request), createResource.savePath() , fieldset));
            }

        } catch (IOException e) {
            return HttpResponse.serverError();
        }
    }

    private Map<String, Object> emptyStringsToNull(Map<String, Object> form) {
        Map<String, Object> result = new HashMap<>(form.keySet().size());
        for (String k : form.keySet()) {
            Object value = form.get(k);
            if (CharSequence.class.isAssignableFrom(value.getClass())) {
                if (StringUtils.isEmpty((CharSequence) value)) {
                    result.put(k, null);
                } else {
                    result.put(k, value);
                }
            } else {
                result.put(k, value);
            }
        }
        return result;
    }

}
