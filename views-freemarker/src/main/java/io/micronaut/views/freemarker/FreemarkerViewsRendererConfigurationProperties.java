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
package io.micronaut.views.freemarker;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import io.micronaut.context.annotation.*;
import io.micronaut.core.convert.format.MapFormat;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.core.naming.conventions.StringConvention;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsConfigurationProperties;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import java.util.Properties;

/**
 * {@link ConfigurationProperties} implementation of {@link FreemarkerViewsRendererConfiguration}.
 * 
 * All configured properties are extracted from {@link freemarker.template.Configuration} and
 * {@link freemarker.core.Configurable}. All Freemarker properties names are reused in the micronaut
 * configuration.
 * 
 * If a value is not declared and is null, the default configuration from Freemarker is used. The expected
 * format of each value is the same from Freemarker, and no conversion or validation is done by Micronaut.
 * 
 * All Freemarker configuration documentation is published in their
 * <a href="https://freemarker.apache.org/docs/pgui_config.html">site</a>.
 * 
 * @author Jerónimo López
 * @since 1.1
 */
@Requires(classes = freemarker.template.Configuration.class)
@ConfigurationProperties(FreemarkerViewsRendererConfigurationProperties.PREFIX)
public class FreemarkerViewsRendererConfigurationProperties extends Configuration implements FreemarkerViewsRendererConfiguration {

    public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".freemarker";

    /**
     * The default extension.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_EXTENSION = "ftl";

    /**
     * The default enable value.
     */
    @SuppressWarnings("WeakerAccess")
    public static final boolean DEFAULT_ENABLED = true;

    private boolean enabled = DEFAULT_ENABLED;
    private String defaultExtension = DEFAULT_EXTENSION;

    /**
     * Default contructor.
     *
     * @param viewsConfiguration The views configuration
     * @param version The minimum version
     * @param resourceLoader The resource loader
     */
    public FreemarkerViewsRendererConfigurationProperties(
            ViewsConfiguration viewsConfiguration,
            @Property(name = PREFIX + ".incompatible-improvements") @Nullable String version,
            @Nullable ClassPathResourceLoader resourceLoader) {
        super(version != null ? new Version(version) : Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        if (resourceLoader != null) {
            setClassLoaderForTemplateLoading(
                    resourceLoader.getClassLoader(), "/" + viewsConfiguration.getFolder()
            );
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Whether freemarker views are enabled.
     *
     * @param enabled True if they are.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return The default extension to use
     */
    public @NonNull String getDefaultExtension() {
        return defaultExtension;
    }

    /**
     * Sets the default extension to use.
     *
     * @param defaultExtension The default extension
     */
    public void setDefaultExtension(String defaultExtension) {
        ArgumentUtils.requireNonNull("defaultExtension", defaultExtension);
        this.defaultExtension = defaultExtension;
    }

    /**
     * @return An optional version number
     */
    public Version getIncompatibleImprovements() {
        return super.getIncompatibleImprovements();
    }

    /**
     * the FreeMarker version number where the not 100% backward compatible bug fixes
     * and improvements that you want to enable were already implemented. Defaults to
     * {@link Configuration#DEFAULT_INCOMPATIBLE_IMPROVEMENTS}.
     *
     * @param incompatibleImprovements The version number
     */
    public void setIncompatibleImprovements(Version incompatibleImprovements) {
        super.setIncompatibleImprovements(incompatibleImprovements);
    }

    @Override
    public void setSettings(@MapFormat(keyFormat = StringConvention.UNDER_SCORE_SEPARATED_LOWER_CASE) Properties props) throws TemplateException {
        super.setSettings(props);
    }
}
