package io.micronaut.views.fields.controllers;

import io.micronaut.context.BeanContext;
import io.micronaut.context.ExecutionHandleLocator;
import io.micronaut.context.LocalizedMessageSource;
import io.micronaut.context.Qualifier;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.reflect.ClassUtils;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.LocaleResolver;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.ExecutableMethod;
import io.micronaut.inject.ExecutionHandle;
import io.micronaut.inject.MethodExecutionHandle;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.fields.controllers.createsave.CreateHtmlResource;
import io.micronaut.views.fields.controllers.createsave.CreateSaveController;
import io.micronaut.views.fields.controllers.createsave.SaveService;
import io.micronaut.views.fields.controllers.createsave.SaveServiceFactory;
import io.micronaut.views.fields.controllers.editupdate.EditHtmlResource;
import io.micronaut.views.fields.controllers.editupdate.EditService;
import io.micronaut.views.fields.controllers.editupdate.EditUpdateController;
import io.micronaut.views.fields.controllers.editupdate.UpdateServiceFactory;
import io.micronaut.views.fields.controllers.read.*;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Singleton
class HtmlResourceRouteBuilder extends DefaultRouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlResourceRouteBuilder.class);
    private static final String METHOD_NAME_CREATE = "create";
    private static final String METHOD_NAME_SAVE = "save";

    private static final String METHOD_NAME_EDIT = "edit";
    private static final String METHOD_NAME_UPDATE = "update";
    public static final String METHOD_NAME_SHOW = "show";

    HtmlResourceRouteBuilder(ExecutionHandleLocator executionHandleLocator,
                           BeanContext beanContext,
                             List<ReadHtmlResource> readResources,
                           List<CreateHtmlResource> createResources,
                           List<EditHtmlResource> editResources) {
        super(executionHandleLocator);
        processReadResources(beanContext, readResources);
        processCreateResources(beanContext, createResources);
        processEditResources(beanContext, editResources);


    }
    private void processReadResources(BeanContext beanContext,
                                      List<ReadHtmlResource> resources) {
        for (ReadHtmlResource resource : resources) {
            Qualifier nameQualifer = Qualifiers.byName(resource.getName());
            registerReadService(beanContext, nameQualifer, resource);
            if (beanContext.containsBean(ReadService.class, nameQualifer)) {
                Optional<ShowController> controllerOptional = beanContext.findBean(ShowController.class, nameQualifer);
                controllerOptional.ifPresent(controller -> {
                    BeanDefinition<ShowController> bd = beanContext.getBeanDefinition(ShowController.class, nameQualifer);
                    buildShowRoute(resource, bd, controller);
                });
            }
        }
    }

    private void buildShowRoute(ReadHtmlResource resource, BeanDefinition<ShowController> bd, ShowController controller) {
        bd.findMethod(METHOD_NAME_SHOW, HttpRequest.class, Object.class).ifPresent(m -> {
            String path = resource.showPath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering edit route [GET: {}]", path);
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            MethodExecutionHandle<Object, Object> executionHandle = ExecutionHandle.of(controller, (ExecutableMethod) m);
            buildRoute(HttpMethod.GET, path, executionHandle);
        });
    }
    private <E> void registerReadService(BeanContext beanContext,
                                       Qualifier qualifier,
                                       ReadHtmlResource<E> resource) {
        if (!beanContext.containsBean(ReadService.class, qualifier)) {
            for (ReadServiceFactory factory : beanContext.getBeansOfType(ReadServiceFactory.class)) {
                if (factory.registerSingleton(resource)) {
                    break;
                }
            }
        }
    }


    private void processEditResources(BeanContext beanContext, List<EditHtmlResource> editResources) {
        for (EditHtmlResource editResource : editResources) {
            Qualifier qualifier = Qualifiers.byName(editResource.getName());
            registerUpdateService(beanContext, qualifier, editResource);
            if (beanContext.containsBean(EditService.class, qualifier)) {
                Optional<EditUpdateController> controllerOptional = beanContext.findBean(EditUpdateController.class, qualifier);
                controllerOptional.ifPresent(controller -> {
                    BeanDefinition<EditUpdateController> bd = beanContext.getBeanDefinition(EditUpdateController.class, qualifier);
                    buildEditRoute(editResource, bd, controller);
                    buildUpdateRoute(editResource, bd, controller);
                });
            }
        }
    }

    private void processCreateResources(BeanContext beanContext, List<CreateHtmlResource> createResources) {
        for (CreateHtmlResource createResource : createResources) {
            Qualifier nameQualifer = Qualifiers.byName(createResource.getName());
            registerSaveService(beanContext, nameQualifer, createResource);
            if (beanContext.containsBean(SaveService.class, nameQualifer)) {
                Optional<CreateSaveController> controllerOptional = beanContext.findBean(CreateSaveController.class, nameQualifer);
                controllerOptional.ifPresent(controller -> {
                    BeanDefinition<CreateSaveController> bd = beanContext.getBeanDefinition(CreateSaveController.class, nameQualifer);
                    buildCreateRoute(createResource, bd, controller);
                    buildSaveRoute(createResource, bd, controller);
                });
            }
        }
    }

    private void registerUpdateService(BeanContext beanContext,
                                       Qualifier qualifier,
                                       EditHtmlResource resource) {
        if (!beanContext.containsBean(EditService.class, qualifier)) {
            for (UpdateServiceFactory factory : beanContext.getBeansOfType(UpdateServiceFactory.class)) {
                if (factory.registerSingleton(resource)) {
                    break;
                }
            }
        }
    }

    private void registerSaveService(BeanContext beanContext,
                                     Qualifier nameQualifer,
                                     CreateHtmlResource createResource) {
        if (!beanContext.containsBean(SaveService.class, nameQualifer)) {
            for (SaveServiceFactory factory : beanContext.getBeansOfType(SaveServiceFactory.class)) {
                Optional<SaveService> saveServiceOptional = factory.create(createResource);
                if (saveServiceOptional.isPresent()) {
                    SaveService saveService = saveServiceOptional.get();
                    beanContext.registerSingleton(SaveService.class, saveService, nameQualifer);
                    break;
                }
            }
        }
    }

    private void buildSaveRoute(CreateHtmlResource createResource, BeanDefinition<CreateSaveController> bd, CreateSaveController controller) {
        bd.findMethod(METHOD_NAME_SAVE, HttpRequest.class, Map.class).ifPresent(m -> {
            String savePath = createResource.savePath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering save route [POST: {}]", savePath);
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            MethodExecutionHandle<Object, Object> executionHandle = ExecutionHandle.of(controller, (ExecutableMethod) m);
            buildRoute(HttpMethod.POST, savePath, Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE), executionHandle);
        });
    }

    private void buildCreateRoute(CreateHtmlResource resource, BeanDefinition<CreateSaveController> bd, CreateSaveController controller) {
        bd.findMethod(METHOD_NAME_CREATE, HttpRequest.class).ifPresent(m -> {
            String path = resource.createPath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering create route [GET: {}]", path);
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            MethodExecutionHandle<Object, Object> executionHandle = ExecutionHandle.of(controller, (ExecutableMethod) m);
            buildRoute(HttpMethod.GET, path, executionHandle);
        });
    }

    private void buildEditRoute(EditHtmlResource resource, BeanDefinition<EditUpdateController> bd, EditUpdateController controller) {
        bd.findMethod(METHOD_NAME_EDIT, HttpRequest.class, Object.class).ifPresent(m -> {
            String path = resource.editPath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering edit route [GET: {}]", path);
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            MethodExecutionHandle<Object, Object> executionHandle = ExecutionHandle.of(controller, (ExecutableMethod) m);
            buildRoute(HttpMethod.GET, path, executionHandle);
        });
    }

    private void buildUpdateRoute(EditHtmlResource createResource, BeanDefinition<EditUpdateController> bd, EditUpdateController controller) {
        bd.findMethod(METHOD_NAME_UPDATE, HttpRequest.class, Map.class).ifPresent(m -> {
            String savePath = createResource.updatePath();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Registering save route [POST: {}]", savePath);
            }
            @SuppressWarnings({"unchecked", "rawtypes"})
            MethodExecutionHandle<Object, Object> executionHandle = ExecutionHandle.of(controller, (ExecutableMethod) m);
            buildRoute(HttpMethod.POST, savePath, Collections.singletonList(MediaType.APPLICATION_FORM_URLENCODED_TYPE), executionHandle);
        });
    }


}
