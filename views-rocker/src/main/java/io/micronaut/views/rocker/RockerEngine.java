/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.rocker;

import com.fizzed.rocker.BindableRockerModel;
import com.fizzed.rocker.Rocker;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.TemplateNotFoundException;

import static io.micronaut.views.ViewUtils.normalizeFile;

/**
 * Engine for Rocker templates.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
public class RockerEngine {
    
    private final String path;
    private final String extension;
    
    /**
     * Creates a new instance of the rocker engine.
     *
     * @param path The base path templates are stored under
     * @param extension The file extension used by the templates
     */
    public RockerEngine(String path, String extension) {
        this.path = path;
        this.extension = extension;
    }
    
    /**
     * Checks to see if a template exists.
     *
     * @param viewName The name of the template
     * @return True if the template exists, false otherwise
     */
    public boolean exists(String viewName) {
        try {
            template(viewName);
        } catch (TemplateNotFoundException | TemplateBindException e) {
            return false;
        }
        return true;
    }
    
    /**
     * Loads the template.
     *
     * @param viewName The name of the template
     * @return The template
     */
    public BindableRockerModel template(String viewName) {
        return Rocker.template(templateName(viewName));
    }
    
    private String templateName(final String name) {
        return path + normalizeFile(name, extension) + "." + extension;
    }

}
