package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.*;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.StringUtils;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Introspected
public class BreadcrumbNav extends DelegateHtmlElement {
    protected BreadcrumbNav(HtmlElement htmlElement) {
        super(htmlElement);
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder extends HtmlElementBuilder<Builder> {
        private @NonNull List<@NonNull Breadcrumb> breadcrumbs = new ArrayList<>();

        public @NonNull Builder breadcrumb(@NonNull Breadcrumb breadcrumb) {
            breadcrumbs.add(breadcrumb);
            return this;
        }

        public @NonNull BreadcrumbNav build() {
            return new BreadcrumbNav(createNav(breadcrumbs));
        }
    }

    private static @NonNull Nav createNav(@NonNull List<@NonNull Breadcrumb> breadcrumbs) {
        OrderedList.Builder orderListBuilder = OrderedList.builder()
                .classAttribute("breadcrumb");
        for (Breadcrumb breadcrumb : breadcrumbs) {
            orderListBuilder.li(createListItem(breadcrumb));
        }
        return Nav.builder()
                .attribute("aria-label", "breadcrumb")
                .element(orderListBuilder.build()).build();
    }

    private static @NonNull ListItem createListItem(@NonNull Breadcrumb breadcrumb) {
        ListItem.Builder listItemBuilder = ListItem.builder();
        if (breadcrumb.active()) {
            listItemBuilder.classAttribute("breadcrumb-item active")
                    .attribute("aria-current", "page");
        } else {
            listItemBuilder.classAttribute("breadcrumb-item");
        }
        if (StringUtils.isEmpty(breadcrumb.href())) {
            listItemBuilder.content(breadcrumb.text());
        } else {
            listItemBuilder.element(Anchor.builder().href(breadcrumb.href()).content(breadcrumb.text()).build());
        }
        return listItemBuilder.build();
    }
}
