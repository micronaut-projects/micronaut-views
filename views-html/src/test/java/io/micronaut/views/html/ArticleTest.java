package io.micronaut.views.html;

import io.micronaut.core.beans.BeanIntrospection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArticleTest {

    @Test
    void isAnnotatedWithIntrospected() {
        assertDoesNotThrow(() -> BeanIntrospection.getIntrospection(Article.class));
    }

    @Test
    void toHtml() {
        String html = Article.builder()
                .classAttribute("post")
                .content("Article content")
                .build()
                .toHtml();
        assertEquals("<article class=\"post\">Article content</article>", html);
    }
}
