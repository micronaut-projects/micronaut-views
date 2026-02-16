package io.micronaut.views.html.bootstrap;

import io.micronaut.core.beans.BeanIntrospection;
import io.micronaut.http.uri.UriBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Pagination.class));
    }

    @Test
    void toHtml() {
        String html = Pagination.builder()
                .ariaLabel("Games Pages")
                .currentPage(2)
                .pages(3)
                .max(7)
                .link(i -> UriBuilder.of("/games")
                        .path("/list")
                        .queryParam("page", "" + i)
                        .build()
                        .toString())
                .justifyContentCenter()
                .build()
                .toHtml();
        String expected = """
                <nav aria-label="Games Pages"><ul class="pagination justify-content-center"><li class="page-item"><a class="page-link" href="/games/list?page=1" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li><li class="page-item"><a class="page-link" href="/games/list?page=1">1</a></li><li class="page-item active"><a class="page-link" href="/games/list?page=2" aria-current="page">2</a></li><li class="page-item"><a class="page-link" href="/games/list?page=3">3</a></li><li class="page-item"><a class="page-link" href="/games/list?page=3" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li></ul></nav>""";
        assertEquals(expected, html);
    }

    @Test
    void toHtmlReturnsEmptyStringWhenOnlyOnePage() {
        String html = Pagination.builder()
                .ariaLabel("Games Pages")
                .currentPage(1)
                .pages(3)
                .max(1)
                .link(i -> UriBuilder.of("/games")
                        .path("/list")
                        .queryParam("page", "" + i)
                        .build()
                        .toString())
                .build()
                .toHtml();
        assertTrue(html.isEmpty());
    }

    @Test
    void toHtmlReturnsEmptyStringWhenZeroPages() {
        String html = Pagination.builder()
                .ariaLabel("Games Pages")
                .currentPage(1)
                .pages(3)
                .max(0)
                .link(i -> UriBuilder.of("/games")
                        .path("/list")
                        .queryParam("page", "" + i)
                        .build()
                        .toString())
                .build()
                .toHtml();
        assertTrue(html.isEmpty());
    }

}
