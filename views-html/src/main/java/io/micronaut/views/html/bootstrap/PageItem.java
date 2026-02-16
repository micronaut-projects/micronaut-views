package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.Anchor;
import io.micronaut.views.html.DelegateHtmlElement;
import io.micronaut.views.html.HtmlElement;
import io.micronaut.views.html.ListItem;
import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;

@Introspected
public class PageItem extends DelegateHtmlElement {
    protected PageItem(HtmlElement htmlElement) {
        super(htmlElement);
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String href;

        public @NonNull Builder text(@NonNull String text) {
            this.text = text;
            return this;
        }

        public @NonNull Builder href(@NonNull String href) {
            this.href = href;
            return this;
        }

        public @NonNull PageItem build() {
            return new PageItem(ListItem.builder()
                    .classAttribute("page-item")
                    .element(Anchor.builder().href(href)
                            .classAttribute("page-link")
                            .href(href)
                            .content(text)
                            .build())
                    .build());
        }
    }
}
