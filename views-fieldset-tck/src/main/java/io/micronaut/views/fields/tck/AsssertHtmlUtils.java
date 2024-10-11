package io.micronaut.views.fields.tck;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class AsssertHtmlUtils {

    private AsssertHtmlUtils() {

    }

    public static void assertHtmlEquals(String expected, String html) {
        String expectedCleanup = cleanup(expected);
        String htmlCleanup = cleanup(html);
        assertEquals(expectedCleanup, htmlCleanup);
    }

    private static String cleanup(String html) {
        String cleanup = html.replaceAll("\\s+","");
        cleanup = cleanup.replaceAll("value=\"\"", "");
        cleanup = cleanup.replaceAll("selected=\"selected\"", "selected");
        cleanup = cleanup.replaceAll("required=\"required\"", "required");
        cleanup = cleanup.replaceAll("checked=\"checked\"", "checked");
        return cleanup;
    }
}
