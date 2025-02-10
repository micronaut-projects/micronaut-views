/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.fields.tck;

import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewsRenderer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings({"MissingJavadocType", "java:S5960"}) // Assertions are fine, these are tests
public final class TestUtils {
    private TestUtils() {
    }

    public static String render(String viewName, ViewsRenderer<Map<String, Object>, ?> viewsRenderer, Map<String, Object> model) throws IOException {
        return output(viewsRenderer.render(viewName, model, null));
    }

    public static void assertEqualsIgnoreSpace(String expected, String value) {
        assertEquals(expected.replaceAll("\\s+", ""), value.replaceAll("\\s+", ""));
    }

    public static String output(Writable writeable) throws IOException {
        StringWriter sw = new StringWriter();
        writeable.writeTo(sw);
        return sw.toString();
    }

    public static int countOccurrences(String mainString, String subString) {
        if (mainString == null || subString == null || subString.isEmpty()) {
            return 0;
        }

        int count = 0;
        int index = 0;

        while ((index = mainString.indexOf(subString, index)) != -1) {
            count++;
            index += subString.length();
        }

        return count;
    }
}
