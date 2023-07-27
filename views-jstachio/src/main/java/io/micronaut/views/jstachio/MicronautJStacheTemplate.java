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
package io.micronaut.views.jstachio;

import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateModel;
import io.micronaut.views.ModelAndView;

/**
 * JStachio supports making generated templates implement an interface
 * and this "mixin" adds a method to convert the template (this)
 * and passed in model to a Micronauth {@link ModelAndView}.
 * <p>
 * If <code>using</code> {@link MicronautJStacheConfig} then generated
 * templates will automatically implement this interface.
 * 
 * @param <T> the model type
 * @author agentgt
 */
public interface MicronautJStacheTemplate<T> extends Template<T> {

    /**
     * Takes the model and convertes it to ModelAndView with this
     * template. JStachio does not have to go looking up the
     * template matching the model with this method.
     * @param object model object (annotated with JStache)
     * @return JStachio ModelAndView.
     */
    default ModelAndView<TemplateModel> toModelAndView(T object) {
        return JStachioModelAndView.of(model(object));
    }
}
