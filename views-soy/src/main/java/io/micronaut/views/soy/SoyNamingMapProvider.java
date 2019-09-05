package io.micronaut.views.soy;


import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;

import javax.annotation.Nullable;


/**
 * Specifies an interface that provides renaming maps for CSS and XID calls in Soy templates.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface SoyNamingMapProvider {
  /**
   * Provide an optional Soy renaming map for CSS class calls. Default implementation just returns `null`, which opts-
   * out of CSS class renaming.
   *
   * @return CSS renaming map.
   */
  default @Nullable SoyCssRenamingMap cssRenamingMap() {
    return null;
  }

  /**
   * Provide an optional Soy ID renaming map for CSS IDs. Default implementation just returns `null`, which opts-out of
   * XID renaming.
   *
   * @return XID renaming map.
   */
  default @Nullable SoyIdRenamingMap idRenamingMap() {
    return null;
  }
}
