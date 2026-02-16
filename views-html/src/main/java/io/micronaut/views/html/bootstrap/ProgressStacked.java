package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.DelegateHtmlElement;
import io.micronaut.views.html.Div;
import io.micronaut.views.html.HtmlElement;
import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@Introspected
public class ProgressStacked extends DelegateHtmlElement {
    protected ProgressStacked(HtmlElement htmlElement) {
        super(htmlElement);
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final @NonNull List<@NonNull Progressbar> progressbars = new ArrayList<>();


        public @NonNull Builder progressBar(@NonNull Progressbar progressbar) {
            progressbars.add(progressbar);
            return this;
        }

        public @NonNull ProgressStacked build() {
            Div.Builder builder = Div.builder().classAttribute("progress-stacked");
            for (Progressbar progressbar : progressbars) {
                builder.element(progressbar);
            }
            return new ProgressStacked(builder.build());
        }
    }
}
