/*
 * Copyright 2017-2020 original authors
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
package io.micronaut.views.rocker;

import com.fizzed.rocker.BindableRockerModel;
import com.fizzed.rocker.Rocker;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.TemplateNotFoundException;
import com.fizzed.rocker.runtime.DefaultRockerBootstrap;
import io.micronaut.core.annotation.Experimental;
import io.micronaut.views.ViewsSourceRoots;
import io.micronaut.views.exceptions.ViewRenderingException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static io.micronaut.views.ViewUtils.normalizeFile;

/**
 * Engine for Rocker templates.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
public class RockerEngine implements AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(RockerEngine.class);

    private final String path;
    private final String extension;
    private final @Nullable ClassLoader classLoader;
    private final @Nullable SourceRootsTemplates sourceRootsTemplates;
    
    /**
     * Creates a new instance of the rocker engine.
     *
     * @param path The base path templates are stored under
     * @param extension The file extension used by the templates
     */
    public RockerEngine(String path, String extension) {
        this.path = path;
        this.extension = extension;
        this.classLoader = null;
        this.sourceRootsTemplates = null;
    }

    /**
     * Creates the engine of development mode: a template of the views source roots is compiled from there at
     * runtime, and compiled again after a change, when {@code com.fizzed:rocker-compiler} is on the class path.
     * Any other template is the class the build generated, loaded through the application's class loader, so that
     * it follows the class reload.
     *
     * @param path The base path templates are stored under
     * @param extension The file extension used by the templates
     * @param sourceRoots The views source roots
     * @param classLoader The application's class loader
     * @since 6.4.0
     */
    @Experimental
    public RockerEngine(String path, String extension, ViewsSourceRoots sourceRoots, ClassLoader classLoader) {
        this.path = path;
        this.extension = extension;
        this.classLoader = classLoader;
        if (sourceRoots.isEnabled() && RockerSourceCompiler.isAvailable(classLoader)) {
            this.sourceRootsTemplates = new RockerSourceCompiler(path, sourceRoots, classLoader);
        } else {
            if (sourceRoots.isEnabled()) {
                LOG.info("Rocker templates are the classes the build generates: an edited template is rendered once the build compiles it. Add com.fizzed:rocker-compiler as a development-only dependency, and run on a JDK, to compile the templates of the views source roots at runtime instead.");
            }
            this.sourceRootsTemplates = null;
        }
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
        } catch (ViewRenderingException e) {
            // a template of the source roots that does not compile: rendering it reports why
            return true;
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
        String templatePath = templateName(viewName);
        if (sourceRootsTemplates != null) {
            Optional<RockerModel> model = sourceRootsTemplates.model(templatePath);
            if (model.isPresent()) {
                return new BindableRockerModel(templatePath, model.get().getClass().getCanonicalName(), model.get());
            }
        }
        if (classLoader != null) {
            return new BindableRockerModel(templatePath, DefaultRockerBootstrap.templatePathToClassName(templatePath), precompiled(templatePath, classLoader));
        }
        return Rocker.template(templatePath);
    }

    /**
     * Compiles the templates of the views source roots again on the next render, in development mode.
     */
    void invalidate() {
        if (sourceRootsTemplates != null) {
            sourceRootsTemplates.invalidate();
        }
    }

    /**
     * Deletes the templates compiled at runtime, in development mode.
     *
     * @since 6.4.0
     */
    @Override
    public void close() {
        if (sourceRootsTemplates != null) {
            sourceRootsTemplates.close();
        }
    }

    /**
     * The class the build generated, loaded through the application's class loader rather than Rocker's, which
     * under the development launcher is the library tier's and does not see the application's classes.
     */
    private static RockerModel precompiled(String templatePath, ClassLoader classLoader) {
        String className = DefaultRockerBootstrap.templatePathToClassName(templatePath);
        Class<?> modelType;
        try {
            modelType = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new TemplateNotFoundException("Compiled template " + templatePath + " not found", e);
        }
        try {
            return (RockerModel) modelType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException | ClassCastException | LinkageError e) {
            throw new TemplateBindException(templatePath, className, "Unable to create model for template " + templatePath, e);
        }
    }
    
    private String templateName(final String name) {
        return path + normalizeFile(name, extension) + "." + extension;
    }
}
