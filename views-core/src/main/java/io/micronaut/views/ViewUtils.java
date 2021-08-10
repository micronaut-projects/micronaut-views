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
package io.micronaut.views;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.beans.BeanMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for views.
 *
 * @author James Kleeh
 * @since 1.1.0
 */
public class ViewUtils {
    /**
     * Extension separator.
     */
    public static final String EXTENSION_SEPARATOR = ".";

    /**
     * Creates a view model for the given data.
     * @param data The data
     * @return The model
     */
    @NonNull
    public static Map<String, Object> modelOf(@Nullable Object data) {
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
     * separators that starts and ends with a "/".
     *
     * @param path The path to normalizeFile
     * @return The normalized path
     */
    @NonNull
    public static String normalizeFolder(@Nullable String path) {
        if (path == null) {
            path = "";
        } else {
            path = normalizeFile(path, null);
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    /**
     * Returns a path that is converted to unix style file separators
     * and never starts with a "/". If an extension is provided and the
     * path ends with the extension, the extension will be stripped.
     * The extension parameter supports extensions that do and do not
     * begin with a ".".
     *
     * @param path The path to normalizeFile
     * @param extension The file extension
     * @return The normalized path
     */
    @NonNull
    public static String normalizeFile(@NonNull String path, String extension) {
        path = path.replace("\\", "/");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (extension != null && !extension.startsWith(".")) {
            extension = "." + extension;
            if (path.endsWith(extension)) {
                int idx = path.indexOf(extension);
                path = path.substring(0, idx);
            }
        }
        return path;
    }
}
