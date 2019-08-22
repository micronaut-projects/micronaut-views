package io.micronaut.views.soy;


import com.google.template.soy.SoyFileSet;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsConfigurationProperties;

import javax.annotation.Nonnull;


/**
 * {@link ConfigurationProperties} implementation for {@link SoyTofuViewsRenderer}.
 *
 * Configured properties support a {@link SoyFileSet}, which is rendered via a from-source renderer. Template sources
 * are provided via DI, using a {@link SoyFileSetProvider}.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@ConfigurationProperties(SoyTofuViewsRendererConfigurationProperties.PREFIX)
public class SoyTofuViewsRendererConfigurationProperties implements SoyTofuViewsRendererConfiguration {

  public static final String PREFIX = ViewsConfigurationProperties.PREFIX + ".soy";

  /**
   * The default enable value.
   */
  @SuppressWarnings("WeakerAccess")
  public static final boolean DEFAULT_ENABLED = true;

  private boolean enabled = DEFAULT_ENABLED;
  private SoyFileSetProvider fileSetProvider;

  /**
   * Default constructor for Soy views renderer config properties.
   *
   * @param viewsConfiguration The views configuration
   * @param fileSetProvider Soy file set provider (template sources)
   */
  public SoyTofuViewsRendererConfigurationProperties(
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
   * @return The Soy file set to render from
   */
  public @Nonnull SoyFileSet getFileSet() {
    return fileSetProvider.provideSoyFileSet();
  }

}
