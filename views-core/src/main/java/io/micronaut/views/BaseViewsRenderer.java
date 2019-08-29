package io.micronaut.views;


import io.micronaut.core.beans.BeanMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Base views renderer interface, shared by both the synchronous and async renderers.
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
  boolean exists(@Nonnull String viewName);

  /**
   * Creates a view model for the given data.
   * @param data The data
   * @return The model
   */
  default @Nonnull Map<String, Object> modelOf(@Nullable Object data) {
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
  @Nonnull
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
  @Nonnull
  @Deprecated
  default String normalizeFile(@Nonnull String path, String extension) {
    return ViewUtils.normalizeFile(path, extension);
  }
}
