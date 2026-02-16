package io.micronaut.views.html.bootstrap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BreadcrumbNavTest {

    @Test
    void breadcrumbNavToHtml() {
        assertEquals("""
                <nav aria-label="breadcrumb"><ol class="breadcrumb"><li class="breadcrumb-item active" aria-current="page">Home</li></ol></nav>""",
                BreadcrumbNav.builder().breadcrumb(new Breadcrumb("Home", Boolean.TRUE)).build().toHtml());
    }
}
