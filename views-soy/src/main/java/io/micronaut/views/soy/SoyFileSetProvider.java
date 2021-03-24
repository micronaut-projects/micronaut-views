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

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;
import io.micronaut.core.annotation.Nullable;

/**
 * Interface via DI to acquire a {@link SoyFileSet}.
 *
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.2.1
 */
public interface SoyFileSetProvider {
  /**
   * @return Soy file set to render templates with
   */
  @Nullable SoyFileSet provideSoyFileSet();

  /**
   * @return Compiled set of Soy templates, if supported
   */
  default @Nullable SoySauce provideCompiledTemplates() {
    return null;
  }
}
