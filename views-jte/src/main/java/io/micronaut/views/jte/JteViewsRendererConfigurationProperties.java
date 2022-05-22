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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.views.ViewsConfigurationProperties;

/**
 * ConfigurationProperties for JTE.
 *
 * @author edward3h
 * @since 3.1.0
 */
@ConfigurationProperties(JteViewsRendererConfigurationProperties.PREFIX)
public final class JteViewsRendererConfigurationProperties implements JteViewsRendererConfiguration {
    @SuppressWarnings("WeakerAccess")
    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".jte";

    /**
     * The default dynamic value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_DYNAMIC = false;

    /**
     * The default dynamic value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_DYNAMIC_PATH = "build/jte-classes";

    public static final boolean DEFAULT_BINARY_STATIC_CONTENT = false;

    private boolean dynamic = DEFAULT_DYNAMIC;
    private String dynamicPath = DEFAULT_DYNAMIC_PATH;
    private boolean binaryStaticContent = DEFAULT_BINARY_STATIC_CONTENT;

    /**
     * Whether to enable dynamic reloading of templates. Default value ({@value #DEFAULT_DYNAMIC}).
     * @param dynamic true to enable dynamic reloading
     */
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * Root directory under which to write generated source and class files. . Default value ({@value #DEFAULT_DYNAMIC_PATH}).
     * @param path the directory
     */
    public void setDynamicPath(String path) {
        this.dynamicPath = path;
    }

    @Override
    public String getDynamicPath() {
        return dynamicPath;
    }

    @Override
    public boolean isBinaryStaticContent() {
        return binaryStaticContent;
    }

    /**
     * Enable building binary content for templates. Default value ({@value #DEFAULT_BINARY_STATIC_CONTENT}).
     * (Only has an effect when 'dynamic' is true. To use with precompiled templates, enable it in the build plugin)
     * @param binaryStaticContent true to enable binary content
     */
    public void setBinaryStaticContent(boolean binaryStaticContent) {
        this.binaryStaticContent = binaryStaticContent;
    }
}
