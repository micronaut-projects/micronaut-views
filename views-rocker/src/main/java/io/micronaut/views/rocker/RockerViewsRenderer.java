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
package io.micronaut.views.rocker;

import com.fizzed.rocker.BindableRockerModel;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.views.AbstractViewsRenderer;
import io.micronaut.views.ViewUtils;
import io.micronaut.views.ViewsConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

/**
 * Renders templates with Rocker.
 *
 * @author Sam Adams
 * @since 1.3.2
 * @param <T> The model type
 * @param <R> The request type
 */
@Singleton
public class RockerViewsRenderer<T, R> extends AbstractViewsRenderer<T, R> {

    protected final RockerEngine rockerEngine;
    protected final ViewsConfiguration viewsConfiguration;
    protected final RockerViewsRendererConfiguration rockerConfiguration;

    /**
     * @param viewsConfiguration  Views Configuration
     * @param rockerConfiguration Rocker Configuration
     * @param rockerEngine        Rocker Engine
     */
    @Inject
    public RockerViewsRenderer(ViewsConfiguration viewsConfiguration,
                               RockerViewsRendererConfiguration rockerConfiguration,
                               RockerEngine rockerEngine) {
        super(rockerConfiguration, viewsConfiguration.getFolder());
        this.viewsConfiguration = viewsConfiguration;
        this.rockerConfiguration = rockerConfiguration;
        this.rockerEngine = rockerEngine;
    }

    @NonNull
    @Override
    public Writable render(@NonNull String view,
                           @Nullable T data,
                           @Nullable R request) {
        ArgumentUtils.requireNonNull("view", view);

        Map<String, Object> context = ViewUtils.modelOf(data);
        BindableRockerModel model = rockerConfiguration.isRelaxed()
                ? rockerEngine.template(view).relaxedBind(context)
                : rockerEngine.template(view).bind(context);
        return new RockerWritable(model);
    }

    @Override
    public boolean exists(@NonNull String viewName) {
        return rockerEngine.exists(viewName);
    }

}
