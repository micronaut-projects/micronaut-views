package io.micronaut.views.soy;


import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;

import javax.annotation.Nullable;


/**
 * Specifies an interface that provides renaming maps for CSS and
 * XID calls in Soy templates.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface SoyNamingMapProvider {
  /**
   * Provide an optional Soy renaming map for CSS class calls.
   *
   * @return CSS renaming map.
   */
  @Nullable SoyCssRenamingMap cssRenamingMap();

  /**
   * Provide an optional Soy ID renaming map for CSS IDs.
   *
   * @return XID renaming map.
   */
  @Nullable SoyIdRenamingMap idRenamingMap();
}
