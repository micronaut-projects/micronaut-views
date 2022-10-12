/*
 * Copyright 2017-2021 original authors
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
package io.micronaut.views.jte;

/**
 * Configuration for JTE views.
 *
 * @author edward3h
 * @since 3.1.0
 */
public interface JteViewsRendererConfiguration {
    /**
     * Use dynamic/hot reload templates.
     * @return true to enable reloading templates.
     */
    boolean isDynamic();

    /**
     * When using dynamic templates, the root directory to write generated source and classes under.
     * @return the directory
     */
    String getDynamicPath();

    /**
     * When using dynamic templates, the root source directory to search. If not specified, jte will
     * search src/main/jte and src/main/resources/&lt;folder&gt; where folder is ViewsConfiguration.getFolder().
     * @return the directory
     */
    String getDynamicSourcePath();

    /**
     * When using dynamic templates, build them with binary content (see https://github.com/casid/jte/blob/master/DOCUMENTATION.md#binary-rendering-for-max-throughput).
     * (When using precompiled templates, this setting is determined by the build configuration.)
     * @return true to enable building binary content
     */
    boolean isBinaryStaticContent();
}
