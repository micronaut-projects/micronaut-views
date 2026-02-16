package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.*;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Introspected
public class Pagination extends DelegateHtmlElement {
    private final int totalPages;

    protected Pagination(HtmlElement htmlElement, int totalPages) {
        super(htmlElement);
        this.totalPages = totalPages;
    }

    @Override
    public @NonNull String toHtml() {
        if (totalPages <= 1) {
            return "";
        }
        return super.toHtml();
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final String DEFAULT_PREVIOUS = "&laquo;";
        private static final String DEFAULT_NEXT = "&raquo;";
        private @NonNull String ariaLabel;
        private @NonNull Integer currentPage;
        private @NonNull Integer pages;
        private @NonNull Integer max = 0;
        private @Nullable String size;
        private @Nullable String justify;
        private @NonNull Function<Integer, String> link;
        private @NonNull String previous = DEFAULT_PREVIOUS;
        private @NonNull String next = DEFAULT_NEXT;

        public @NonNull Builder ariaLabel(@NonNull String ariaLabel) {
            this.ariaLabel = ariaLabel;
            return this;
        }

        public @NonNull Builder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public @NonNull Builder pages(int pages) {
            this.pages = pages;
            return this;
        }

        public @NonNull Builder max(int max) {
            this.max = max;
            return this;
        }

        public @NonNull Builder link(Function<Integer, String> link) {
            this.link = link;
            return this;
        }

        public @NonNull Builder justifyContentCenter() {
            this.justify = "justify-content-center";
            return this;
        }

        public @NonNull Builder justifyContentEnd() {
            this.justify = "justify-content-end";
            return this;
        }

        public @NonNull Builder small() {
            this.size = "sm";
            return this;
        }

        public @NonNull Builder large() {
            this.size = "lg";
            return this;
        }

        public @NonNull Pagination build() {
            Objects.requireNonNull(this.ariaLabel, "provide an ariaLabel to identify it as a navigation section to screen readers and other assistive technologies. For example: Search Results Pages");
            Objects.requireNonNull(this.currentPage, "current page");
            Objects.requireNonNull(this.pages, "you have to provide the number of pages to display");
            Objects.requireNonNull(this.link, "Provide a function with Pagination.Builder::link to create pagination links");
            Objects.requireNonNull(this.max, "Max pages can't be null");
            Nav.Builder builder = Nav.builder();
            if (StringUtils.isNotEmpty(ariaLabel)) {
                builder.attribute("aria-label", ariaLabel);
            }
            List<String> cssClasses = new ArrayList<>();
            cssClasses.add("pagination");
            if (size != null) {
                cssClasses.add("pagination-" + size);
            }
            if (justify != null) {
                cssClasses.add(justify);
            }
            if (max != 0) {
                UnorderedList.Builder ul = UnorderedList.builder()
                        .classAttribute(String.join(" ", cssClasses));

                List<Integer> window = window(currentPage, pages, max);
                if (CollectionUtils.isNotEmpty(window)) {
                    int previousPage = Math.max(0, currentPage - 1);
                    ul.li(navigationListItem(link.apply(previousPage), previous, "Previous", previousPage == 0));
                }
                for (int i : window) {
                    ul.li(listItem(link.apply(i), "" + i, i == currentPage, null));
                }
                if (CollectionUtils.isNotEmpty(window)) {
                    int nextPage = currentPage + 1;
                    ul.li(navigationListItem(link.apply(nextPage), next, "Next", nextPage > max));
                }
                builder.element(ul.build());
            }
            return new Pagination(builder.build(), max);
        }

        private @NonNull ListItem listItem(@NonNull String href,
                                           @NonNull String text,
                                           boolean active,
                                           @Nullable String ariaLabel) {
            Anchor.Builder a = Anchor.builder()
                    .classAttribute("page-link")
                    .content(text)
                    .href(href);
            if (StringUtils.isNotEmpty(ariaLabel)) {
                a.attribute("aria-label", ariaLabel);
            }
            if (active) {
                a.attribute("aria-current", "page");
            }
            return ListItem.builder()
                    .classAttribute(active ? "page-item active" : "page-item")
                    .element(a.build())
                    .build();
        }

        private @NonNull ListItem navigationListItem(@NonNull String href,
                                                     @NonNull String text,
                                                     @Nullable String ariaLabel,
                                                     @NonNull boolean disabled) {
            Anchor.Builder a = Anchor.builder()
                    .classAttribute("page-link")
                    .href(href);
            if (!text.equals(DEFAULT_PREVIOUS) && !text.equals(DEFAULT_NEXT)) {
                a.content(text);
            } else {
                a.element(Span.builder().attribute("aria-hidden", "true").content(text).build());
            }
            if (StringUtils.isNotEmpty(ariaLabel)) {
                a.attribute("aria-label", ariaLabel);
            }
            ListItem.Builder builder = ListItem.builder();
            List<String> cssClasses = new ArrayList<>();
            cssClasses.add("page-item");
            if (disabled) {
                cssClasses.add("disabled");
            }
            return builder
                    .classAttribute(String.join(" ", cssClasses))
                    .element(a.build())
                    .build();
        }

        /**
         * Returns a consecutive window of page numbers of length windowSize,
         * centered on currentPage when possible. If maxPages &gt; 0 the window
         * will not exceed that upper bound.
         */
        public static List<Integer> window(int currentPage,
                                           int windowSize,
                                           int maxPages) {
            if (windowSize <= 0) throw new IllegalArgumentException("windowSize must be > 0");
            if (currentPage <= 0) throw new IllegalArgumentException("currentPage must be > 0");

            int half = windowSize / 2;
            int start = currentPage - half;
            int end = start + windowSize - 1;

            if (start < 1) {
                start = 1;
                end = start + windowSize - 1;
            }

            if (maxPages > 0 && end > maxPages) {
                end = maxPages;
                start = Math.max(1, end - windowSize + 1);
            }

            // final safety: ensure currentPage is inside window (if windowSize is small)
            if (currentPage < start) {
                start = Math.max(1, currentPage - windowSize + 1);
                end = start + windowSize - 1;
                if (maxPages > 0) end = Math.min(end, maxPages);
            }
            if (currentPage > end) {
                end = Math.min(maxPages > 0 ? maxPages : currentPage + half, currentPage + half);
                start = Math.max(1, end - windowSize + 1);
            }

            List<Integer> result = new ArrayList<>();
            for (int i = start; i <= end; i++) result.add(i);
            return result;
        }
    }
}
