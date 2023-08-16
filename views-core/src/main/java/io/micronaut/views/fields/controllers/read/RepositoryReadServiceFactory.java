package io.micronaut.views.fields.controllers.read;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.core.beans.exceptions.IntrospectionException;
import io.micronaut.core.type.Argument;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.fields.controllers.IdUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Requires(classes = CrudRepository.class)
@Singleton
public class RepositoryReadServiceFactory<E> implements ReadServiceFactory<E> {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryReadServiceFactory.class);
    private final BeanContext beanContext;

    public RepositoryReadServiceFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    public boolean registerSingleton(ReadHtmlResource<E> resource) {
        Class<?> clazz = resource.readClass();
        try {
            BeanIntrospection<?> beanIntrospection = BeanIntrospection.getIntrospection(clazz);
            Optional<Class<?>> idTypeOptional = IdUtils.idType(beanIntrospection);
            if (idTypeOptional.isPresent()) {
                Class<?> idType = idTypeOptional.get();
                Argument<?> arg = Argument.of(CrudRepository.class, clazz, idType);
                Optional<?> beanOptional = beanContext.findBean(arg);
                if (beanOptional.isPresent() && beanOptional.get() instanceof CrudRepository crudRepository) {
                    beanContext.registerSingleton(Argument.of(ReadService.class, clazz, idType).getType(),
                        new RepositoryReadService<>(resource, crudRepository),
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
