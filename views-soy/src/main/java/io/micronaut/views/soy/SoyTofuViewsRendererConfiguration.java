package io.micronaut.views.soy;


import com.google.template.soy.SoyFileSet;
import io.micronaut.core.util.Toggleable;


/**
 * Configuration for {@link SoyTofuViewsRenderer}.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface SoyTofuViewsRendererConfiguration extends Toggleable {
  /**
   * @return The backing Soy file set
   */
  SoyFileSet getFileSet();
}
