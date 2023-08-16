/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.fields.controllers.editupdate;

import io.micronaut.context.BeanContext;
import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Executable;
import io.micronaut.core.util.LocaleResolver;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.PathVariable;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EachBean(EditHtmlResource.class)
public class DefaultEditUpdateController<E, ID> implements EditUpdateController<E,ID> {

    private final EditHtmlResource<E> resource;
    private final EditService service;
    private final FieldGenerator fieldGenerator;
    private final FieldsetRenderer fieldsetRenderer;
    private final LocaleResolver<HttpRequest<?>> localeResolver;

    private final JsonMapper jsonMapper;

    public DefaultEditUpdateController(EditHtmlResource<E> resource,
                                       BeanContext beanContext,
                                       FieldGenerator fieldGenerator,
                                       FieldsetRenderer fieldsetRenderer,
                                       LocaleResolver<HttpRequest<?>> localeResolver,
                                       JsonMapper jsonMapper) {
        this.resource = resource;
        this.service = beanContext.getBean(EditService.class, Qualifiers.byName(resource.getName()));
        this.fieldGenerator = fieldGenerator;
        this.fieldsetRenderer = fieldsetRenderer;
        this.localeResolver = localeResolver;
        this.jsonMapper = jsonMapper;
    }

    @Produces(MediaType.TEXT_HTML)
    @Executable
    @Override
    public HttpResponse<?> edit(HttpRequest<?> request, @PathVariable ID id) {
        Optional<E> instanceOptional = ((EditService<E, ID>) service).findById(id);
        if (instanceOptional.isEmpty()) {
            return HttpResponse.notFound();
        }
        E instance = instanceOptional.get();
        Fieldset fieldset = fieldGenerator.generate(instance);
        return HttpResponse.ok(fieldsetRenderer.render(localeResolver.resolveOrDefault(request), resource.updatePath() , fieldset));
    }

    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Executable
    @Override
    public HttpResponse<?> update(HttpRequest<?> request, Map<String, Object> form) {
        try {
            E obj = jsonMapper.readValueFromTree(JsonNode.from(form), resource.editClass());
            try {
                return HttpResponse.seeOther(service.update(obj));
            } catch (ConstraintViolationException e) {
                Fieldset fieldset = fieldGenerator.generate(obj, e);
                return HttpResponse.unprocessableEntity()
                    .contentType(MediaType.TEXT_HTML)
                    .body(fieldsetRenderer.render(localeResolver.resolveOrDefault(request), resource.updatePath(), fieldset));
            }
        } catch (IOException e) {
            return HttpResponse.serverError();
        }
    }
}
