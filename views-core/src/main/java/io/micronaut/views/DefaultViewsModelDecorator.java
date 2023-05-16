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
package io.micronaut.views;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.views.model.ViewModelProcessor;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link ViewsModelDecorator} which enhances a model by running it by all applicable ViewModelProcessors {@link ViewModelProcessor}.
 * @author Sergio del Amo
 * @since 3.0.0
 */
@Singleton
@Requires(classes = HttpRequest.class)
public class DefaultViewsModelDecorator implements ViewsModelDecorator {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultViewsModelDecorator.class);

    private final ApplicationContext applicationContext;

    private final Map<Class<?>, Collection<ViewModelProcessor>> classToProcessors = new ConcurrentHashMap<>();

    public DefaultViewsModelDecorator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Enhances a model by running it by all applicable ViewModelProcessors {@link ViewModelProcessor}.
     *
     * @param request      The http request this model relates to.
     * @param modelAndView The ModelAndView to be enhanced.
     */
    @Override
    public void decorate(HttpRequest<?> request, @NonNull ModelAndView<?> modelAndView) {
        if (modelAndView.getModel().isPresent()) {
            Class<?> modelClass = modelAndView.getModel().get().getClass();
            Collection<ViewModelProcessor> processors = classToProcessors.computeIfAbsent(modelClass,
                    aClass -> applicationContext.getBeansOfType(ViewModelProcessor.class, Qualifiers.byTypeArguments(modelClass)));
            if (LOG.isDebugEnabled()) {
                LOG.debug("located # {} view model processors for class {}", processors.size(), modelClass.getSimpleName());
            }
            processors.forEach(it -> it.process(request, modelAndView));
        }
    }
}
