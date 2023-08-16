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
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.exceptions.DisabledBeanException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.core.type.Argument;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.validation.validator.Validator;
import io.micronaut.views.fields.controllers.IdUtils;
import io.micronaut.views.fields.controllers.createsave.CreateHtmlResource;
import io.micronaut.views.fields.controllers.createsave.RepositorySaveService;
import io.micronaut.views.fields.controllers.createsave.SaveService;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Requires(classes = CrudRepository.class)
@Singleton
public class RepositoryEditServiceFactory<E> implements UpdateServiceFactory<E> {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryEditServiceFactory.class);

    private final BeanContext beanContext;

    public RepositoryEditServiceFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @NonNull
    @Override
    public boolean registerSingleton(@NonNull EditHtmlResource<E> resource) {
        Class<?> clazz = resource.editClass();
        try {
            BeanIntrospection<?> beanIntrospection = BeanIntrospection.getIntrospection(clazz);
            Optional<Class<?>> idTypeOptional = IdUtils.idType(beanIntrospection);
            if (idTypeOptional.isPresent()) {
                Class<?> idType = idTypeOptional.get();
                Argument<?> arg = Argument.of(CrudRepository.class, clazz, idType);
                Optional<?> beanOptional = beanContext.findBean(arg);
                if (beanOptional.isPresent() && beanOptional.get() instanceof CrudRepository crudRepository) {
                    beanContext.registerSingleton(Argument.of(EditService.class, resource.editClass(), idType).getType(),
                        new RepositoryEditService<>(resource, crudRepository, beanContext.getBean(Validator.class)),
                        Qualifiers.byName(resource.getName())
                    );
                    return true;
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No CrudRepository<{},{}>", clazz.getName(), idType.getName());
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No ID type for {}", clazz.getName());
                }
            }
        } catch (IntrospectionException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resource {} is not introspected", clazz.getName());
            }
        }
        return false;
    }
}
