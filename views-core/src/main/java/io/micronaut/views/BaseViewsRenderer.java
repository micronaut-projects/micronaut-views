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
package io.micronaut.views;

import io.micronaut.core.beans.BeanMap;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;



/**
 * Base views renderer interface, shared by both the synchronous and async renderers.
 *
 * @see ViewsRenderer for synchronous view rendering
 * @see ReactiveViewRenderer for async/reactive view rendering
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.3.2
 */
public interface BaseViewsRenderer {

  /**
   * The file separator to use.
   *
   * @deprecated Use {@link File#separator} directly
   */
  @Deprecated
  String FILE_SEPARATOR = File.separator;

  /**
   * The extension separator.
   */
  String EXTENSION_SEPARATOR = ".";

  /**
   * @param viewName view name to be render
   * @return true if a template can be found for the supplied view name.
   */
  boolean exists(@NonNull String viewName);

  /**
   * Creates a view model for the given data.
   * @param data The data
   * @return The model
   */
  default @NonNull Map<String, Object> modelOf(@Nullable Object data) {
    if (data == null) {
      return new HashMap<>(0);
    }
    if (data instanceof Map) {
      return (Map<String, Object>) data;
    }
    return BeanMap.of(data);
  }

  /**
   * Returns a path with unix style folder
   * separators that starts and ends with a "\".
   *
   * @param path The path to normalizeFile
   * @deprecated Use {@link ViewUtils#normalizeFolder(String)} instead
   * @return The normalized path
   */
  @NonNull
  @Deprecated
  default String normalizeFolder(@Nullable String path) {
    return ViewUtils.normalizeFolder(path);
  }

  /**
   * Returns a path that is converted to unix style file separators
   * and never starts with a "\". If an extension is provided and the
   * path ends with the extension, the extension will be stripped.
   * The extension parameter supports extensions that do and do not
   * begin with a ".".
   *
   * @param path The path to normalizeFile
   * @param extension The file extension
   * @deprecated Use {@link ViewUtils#normalizeFile(String, String)} instead
   * @return The normalized path
   */
  @NonNull
  @Deprecated
  default String normalizeFile(@NonNull String path, String extension) {
    return ViewUtils.normalizeFile(path, extension);
  }
}
