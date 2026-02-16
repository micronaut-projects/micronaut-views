package io.micronaut.views.html.bootstrap;

import io.micronaut.views.html.DelegateHtmlElement;
import io.micronaut.views.html.Div;
import io.micronaut.views.html.HtmlElement;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Progressbar extends DelegateHtmlElement {
    protected Progressbar(HtmlElement htmlElement) {
        super(htmlElement);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private @Nullable String ariaLabel;
        private @Nullable Integer ariaValueMin;
        private @Nullable Integer ariaValueMax;
        private @Nullable Integer ariaValueNow;
        private boolean striped;
        private boolean animated;
        private boolean stacked;
        private @Nullable Color color;
        private @Nullable String text;


        public Builder stacked() {
            this.stacked = true;
            return this;
        }

        public Builder stacked(boolean stacked) {
            this.stacked = stacked;
            return this;
        }

        public @NonNull Builder text(@NonNull String text) {
            this.text = text;
            return this;
        }

        public @NonNull Builder color(@NonNull Color color) {
            this.color = color;
            return this;
        }

        public @NonNull Builder dark() {
            return color(Color.DARK);
        }

        public @NonNull Builder light() {
            return color(Color.LIGHT);
        }

        public @NonNull Builder info() {
            return color(Color.INFO);
        }

        public @NonNull Builder warning() {
            return color(Color.WARNING);
        }

        public @NonNull Builder danger() {
            return color(Color.DANGER);
        }

        public @NonNull Builder success() {
            return color(Color.SUCCESS);
        }

        public @NonNull Builder secondary() {
            return color(Color.SECONDARY);
        }

        public @NonNull Builder primary() {
            return color(Color.PRIMARY);
        }

        public @NonNull Builder animated() {
            return animated(true);
        }

        public @NonNull Builder animated(boolean animated) {
            this.animated = animated;
            return this;
        }

        public @NonNull Builder striped() {
            return striped(true);
        }

        public @NonNull Builder striped(boolean striped) {
            this.striped = striped;
            return this;
        }

        public @NonNull Builder ariaLabel(@NonNull String ariaLabel) {
            this.ariaLabel = ariaLabel;
            return this;
        }

        public @NonNull Builder ariaValueMin(@NonNull Integer ariaValueMin) {
            this.ariaValueMin = ariaValueMin;
            return this;
        }

        public @NonNull Builder ariaValueMax(@NonNull Integer ariaValueMax) {
            this.ariaValueMax = ariaValueMax;
            return this;
        }

        public @NonNull Builder ariaValueNow(@NonNull Integer ariaValueNow) {
            this.ariaValueNow = ariaValueNow;
            return this;
        }

        public Progressbar build() {
            Div.Builder divBuilder = Div.builder()
                    .classAttribute("progress")
                    .attribute("role", "progressbar");
            if (ariaValueNow != null && stacked) {
                divBuilder.attribute("style", styleAttribute());
            }
            if (ariaLabel != null) {
                divBuilder.attribute("aria-label", ariaLabel);
            }
            if (ariaValueMin != null) {
                divBuilder.attribute("aria-valuemin", String.valueOf(ariaValueMin));
            }
            if (ariaValueMax != null) {
                divBuilder.attribute("aria-valuemax", String.valueOf(ariaValueMax));
            }
            if (ariaValueNow != null) {
                divBuilder.attribute("aria-valuenow", String.valueOf(ariaValueNow));
            }
            List<String> classAttributes = innerDivClassAttributes();
            Div.Builder innerDiv = Div.builder().classAttribute(String.join(" ", classAttributes));
            if (ariaValueNow != null && !stacked) {
                innerDiv.attribute("style", styleAttribute());
            }

            if (text != null) {
                innerDiv.content(text);
            }
            divBuilder.element(innerDiv.build());
            return new Progressbar(divBuilder.build());
        }

        private String styleAttribute() {
            return String.format("width: %d%%", ariaValueNow);
        }

        private @NonNull List<@NonNull String> innerDivClassAttributes() {
            List<String> classAttributes = new ArrayList<>();
            classAttributes.add("progress-bar");
            if (striped) {
                classAttributes.add("progress-bar-striped");
            }
            if (animated) {
                classAttributes.add("progress-bar-animated");
            }
            if (color != null && text != null) {
                classAttributes.add("text-bg-" + color);
            } else if (color != null) {
                classAttributes.add("bg-" + color);
            }
            return classAttributes;
        }
    }
}
