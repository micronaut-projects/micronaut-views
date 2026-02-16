package io.micronaut.views.html.bootstrap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgressbarTest {

    @Test
    void progressbarTest() {
        String expected = """
        <div class="progress" role="progressbar" aria-label="Danger striped example" aria-valuemin="0" aria-valuemax="100" aria-valuenow="100"><div class="progress-bar progress-bar-striped bg-danger" style="width: 100%"/></div>""";
        String html = Progressbar.builder()
                .ariaLabel("Danger striped example")
                .ariaValueNow(100)
                .ariaValueMax(100)
                .ariaValueMin(0)
                .striped()
                .color(Color.DANGER)
                .build().toHtml();
        assertEquals(expected, html);
    }
}
