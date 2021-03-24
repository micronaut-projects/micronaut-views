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
package io.micronaut.views.soy;

import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import io.micronaut.core.annotation.Nullable;

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
