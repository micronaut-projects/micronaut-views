package io.micronaut.views.fields.controllers.createsave;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.exceptions.DisabledBeanException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.BeanProperty;
import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.core.type.Argument;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.validation.validator.Validator;
import io.micronaut.views.fields.controllers.IdUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Requires(classes = CrudRepository.class)
@Singleton
public class SaveServiceRepositoryFactory<T> implements SaveServiceFactory<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SaveServiceRepositoryFactory.class);

    private final BeanContext beanContext;

    public SaveServiceRepositoryFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    @NonNull
    public Optional<SaveService<T>>  create(@NonNull CreateHtmlResource<T> resource) {
        Class<?> createClass = resource.createClass();
        try {
            BeanIntrospection<?> beanIntrospection = BeanIntrospection.getIntrospection(createClass);
            Optional<Class<?>> idTypeOptional = IdUtils.idType(beanIntrospection);
            if (idTypeOptional.isPresent()) {
                Class<?> idType = idTypeOptional.get();
                Argument<?> arg = Argument.of(CrudRepository.class, resource.createClass(),idType);
                Optional<?> beanOptional = beanContext.findBean(arg);
                if (beanOptional.isPresent() && beanOptional.get() instanceof CrudRepository crudRepository) {
                    return Optional.of(new RepositorySaveService<T>(resource, crudRepository, beanContext.getBean(Validator.class)));
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No CrudRepository<{},{}>", resource.createClass().getName(), idType.getName());
                    }
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No ID type for {}", resource.createClass().getName());
                }
            }
        } catch (IntrospectionException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resource {} is not introspected", resource.createClass().getName());
            }
        }
        return Optional.empty();
    }
}
