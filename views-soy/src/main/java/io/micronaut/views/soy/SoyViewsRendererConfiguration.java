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
import io.micronaut.core.util.Toggleable;

import javax.annotation.Nullable;


/**
 * Configuration for {@link SoyTofuViewsRenderer}.
 *
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.3.0
 */
public interface SoyViewsRendererConfiguration extends Toggleable {
  /**
   * @return The backing Soy file set
   */
  SoyFileSet getFileSet();

  /**
   * @return Whether renaming is enabled
   */
  boolean isRenamingEnabled();

  /**
   * Turns renaming on or off.
   *
   * @param renaming Renaming status.
   */
  void setRenamingEnabled(boolean renaming);

  /**
   * @return Return a set of pre-compiled Soy templates, if supported
   */
  @Nullable default SoySauce getCompiledTemplates() {
    return null;
  }
}
