/*
 * Copyright 2017-2024 original authors
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

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Utils class to cleanup HTML strings to ease comparison.
 */
public final class AsssertHtmlUtils {
    private AsssertHtmlUtils() {
    }

    public static void assertHtmlEquals(String expected, String html) {
        String expectedCleanup = cleanup(expected);
        String htmlCleanup = cleanup(html);
        assertEquals(expectedCleanup, htmlCleanup);
    }

    public static String cleanup(String html) {
        String cleanup = html.replaceAll("\\s+","");
        cleanup = cleanup.replaceAll("value=\"\"", "");
        cleanup = cleanup.replaceAll("disabled=\"disabled\"", "disabled");
        cleanup = cleanup.replaceAll("selected=\"selected\"", "selected");
        cleanup = cleanup.replaceAll("required=\"required\"", "required");
        cleanup = cleanup.replaceAll("checked=\"checked\"", "checked");
        return cleanup;
    }
}
