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
package io.micronaut.views.soy;


import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsConfigurationProperties;

import javax.annotation.Nullable;


/**
 * {@link ConfigurationProperties} implementation for {@link SoyTofuViewsRenderer}.
 *
 * Configured properties support a {@link SoyFileSet}, which is rendered via a from-source renderer. Template sources
 * are provided via DI, using a {@link SoyFileSetProvider}.
 *
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.2.1
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@ConfigurationProperties(SoyViewsRendererConfigurationProperties.PREFIX)
public class SoyViewsRendererConfigurationProperties implements SoyViewsRendererConfiguration {

  public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".soy";

  /**
   * The default enable value.
   */
  @SuppressWarnings("WeakerAccess")
  public static final boolean DEFAULT_ENABLED = true;

  /**
   * Default buffer/chunk size.
   */
  public static final int DEFAULT_CHUNK_SIZE = SoyResponseBuffer.MAX_CHUNK_SIZE;

  /**
   * The default Soy rendering engine.
   */
  @SuppressWarnings("WeakerAccess")
  public static final String DEFAULT_ENGINE = "tofu";

  /**
   * Whether to mount renaming maps.
   */
  @SuppressWarnings("WeakerAccess")
  public static final boolean DEFAULT_RENAMING = true;

  private boolean enabled = DEFAULT_ENABLED;
  private boolean renaming = DEFAULT_RENAMING;
  private int chunkSize = DEFAULT_CHUNK_SIZE;
  private String engine = DEFAULT_ENGINE;
  private SoyFileSetProvider fileSetProvider;

  /**
   * Default constructor for Soy views renderer config properties.
   *
   * @param viewsConfiguration The views configuration
   * @param fileSetProvider Soy file set provider (template sources)
   */
  public SoyViewsRendererConfigurationProperties(
    ViewsConfiguration viewsConfiguration,
    SoyFileSetProvider fileSetProvider) {
    this.fileSetProvider = fileSetProvider;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Whether Soy-backed views are enabled.
   *
   * @param enabled True if they are.
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Specifies whether renaming is enabled. Defaults to `true`.
   *
   * @return True if it is enabled.
   */
  @Override
  public boolean isRenamingEnabled() {
    return renaming;
  }

  /**
   * Turns renaming on or off.
   *
   * @param renaming Renaming status.
   */
  @Override
  public void setRenamingEnabled(boolean renaming) {
    this.renaming = renaming;
  }

  /**
   * @return The Soy file set to render from
   */
  public @Nullable SoyFileSet getFileSet() {
    return fileSetProvider.provideSoyFileSet();
  }

  /**
   * @return Return a set of pre-compiled Soy templates, if supported
   */
  @Nullable @Override
  public SoySauce getCompiledTemplates() {
    return fileSetProvider.provideCompiledTemplates();
  }

  /**
   * @return The current chunk size, used when sizing buffers for render
   */
  public int getChunkSize() {
    return chunkSize;
  }

  /**
   * Set the chunk size for render buffers.
   *
   * @param chunkSize Buffer chunk size
   */
  public void setChunkSize(int chunkSize) {
    this.chunkSize = chunkSize;
  }
}
