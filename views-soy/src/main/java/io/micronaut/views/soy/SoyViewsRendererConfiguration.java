package io.micronaut.views.soy;


import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;
import io.micronaut.core.util.Toggleable;

import javax.annotation.Nullable;


/**
 * Configuration for {@link SoyTofuViewsRenderer}.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface SoyViewsRendererConfiguration extends Toggleable {
  /**
   * @return The backing Soy file set
   */
  SoyFileSet getFileSet();

  /**
   * @return Return a set of pre-compiled Soy templates, if supported
   */
  @Nullable default SoySauce getCompiledTemplates() {
    return null;
  }
}
