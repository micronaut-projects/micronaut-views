package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.DelegateHtmlElement;
import io.micronaut.views.html.Div;
import io.micronaut.views.html.HtmlElement;
import io.micronaut.core.annotation.Introspected;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;


/**
 * <a href="https://getbootstrap.com/docs/5.3/components/alerts/">Alerts</a>
 */
@Introspected
public class Alert extends DelegateHtmlElement {

    protected Alert(@NonNull HtmlElement htmlElement) {
        super(htmlElement);
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private Color color;

        public @NonNull Builder primary(@NonNull String text) {
            this.text = text;
            this.color = Color.PRIMARY;
            return this;
        }

        public @NonNull Builder secondary(@NonNull String text) {
            this.text = text;
            this.color = Color.SECONDARY;
            return this;
        }

        public @NonNull Builder success(@NonNull String text) {
            this.text = text;
            this.color = Color.SUCCESS;
            return this;
        }

        public @NonNull Builder danger(@NonNull String text) {
            this.text = text;
            this.color = Color.DANGER;
            return this;
        }

        public @NonNull Builder warning(@NonNull String text) {
            this.text = text;
            this.color = Color.WARNING;
            return this;
        }

        public @NonNull Builder info(@NonNull String text) {
            this.text = text;
            this.color = Color.INFO;
            return this;
        }

        public @NonNull Builder light(@NonNull String text) {
            this.text = text;
            this.color = Color.LIGHT;
            return this;
        }

        public @NonNull Builder dark(@NonNull String text) {
            this.text = text;
            this.color = Color.DARK;
            return this;
        }

        public @NonNull Alert build() {
            return new Alert(Div.builder()
                    .attribute("role", "alert")
                    .classAttribute(classAttribute())
                    .content(text)
                    .build());
        }

        private @NonNull String classAttribute() {
            List<String> values = new ArrayList<>();
            values.add("alert");
            values.add("alert-" + color);
            return String.join(" ", values);
        }
    }
}
