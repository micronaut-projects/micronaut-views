/*
 * Copyright 2017-2022 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.turbo;

import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.views.View;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import java.util.Optional;

/**
 * Turbo Frame.
 * <a href="https://turbo.hotwired.dev/reference/frames">Turbo Frame</a>
 * @author Sergio del Amo
 * @since 3.4.0
 */
public final class TurboFrame {
    private static final String MEMBER_ACTION = "action";
    private static final String MEMBER_TARGET = "target";
    private static final String MEMBER_ID = "id";
    private static final String MEMBER_SRC = "src";
    private static final String MEMBER_LOADING = "loading";
    private static final String MEMBER_BUSY = "busy";
    private static final String MEMBER_DISABLED = "disabled";
    private static final String MEMBER_AUTOSCROLL = "autoscroll";

    private static final String ATTRIBUTE_DATA_TURBO_ACTION = "data-turbo-action";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String ATTRIBUTE_LOADING = "loading";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_BUSY = "busy";
    private static final String ATTRIBUTE_DISABLED = "disabled";
    private static final String ATTRIBUTE_TARGET = "target";
    private static final String ATTRIBUTE_AUTOSCROLL = "autoscroll";

    private static final String TURBO_FRAME_TAG = "turbo-frame";
    private static final String TURBO_FRAME_CLOSING_TAG = "</turbo-frame>";
    private static final String CLOSE_TAG = ">";
    private static final String OPEN_TAG = "<";
    private static final String SPACE = " ";
    private static final String EQUALS = "=";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String SLASH = "/";

    @Nullable
    private final String id;

    /**
     * Accepts a URL or path value that controls navigation of the element.
     */
    @Nullable
    private final String src;

    @Nullable
    private final Loading loading;

    /**
     * busy is a boolean attribute toggled to be present when a <turbo-frame>-initiated request starts, and toggled false when the request ends.
     */
    @Nullable
    private final Boolean busy;

    /**
     * disabled is a boolean attribute that prevents any navigation when present.
     */
    @Nullable
    private final Boolean disabled;

    /**
     * target refers to another turbo-frame element by ID to be navigated when a descendant `a` is clicked. When `_top`, navigate the window.
     */
    @Nullable
    private final String target;

    /**
     * autoscroll is a boolean attribute that controls whether or not to scroll a <turbo-frame> element (and its descendant <turbo-frame> elements) into view when after loading. Control the scroll’s vertical alignment by setting the data-autoscroll-block attribute to a valid Element.scrollIntoView({ block: “…” }) value: one of "end", "start", "center", or "nearest". When data-autoscroll-block is absent, the default value is "end".
     */
    @Nullable
    private final Boolean autoscroll;

    @Nullable
    private final VisitAction visitAction;

    @Nullable
    private final Object template;

    TurboFrame(@Nullable String id,
               @Nullable String src,
               @Nullable Loading loading,
               @Nullable Boolean busy,
               @Nullable Boolean disabled,
               @Nullable String target,
               @Nullable Boolean autoscroll,
               @Nullable VisitAction visitAction,
               @Nullable Object template) {
        this.id = id;
        this.src = src;
        this.loading = loading;
        this.busy = busy;
        this.disabled = disabled;
        this.target = target;
        this.autoscroll = autoscroll;
        this.visitAction = visitAction;
        this.template = template;
    }

    @NonNull
    public Optional<String> getId() {
        return Optional.ofNullable(this.id);
    }

    @NonNull
    public Optional<Boolean> getBusy() {
        return Optional.ofNullable(this.busy);
    }

    @NonNull
    public Optional<Boolean> getDisabled() {
        return Optional.ofNullable(this.disabled);
    }

    @NonNull
    public Optional<Boolean> getAutoScroll() {
        return Optional.ofNullable(this.autoscroll);
    }

    @NonNull
    public Optional<Loading> getLoading() {
        return Optional.ofNullable(this.loading);
    }

    @NonNull
    public Optional<String> getSrc() {
        return Optional.ofNullable(this.src);
    }

    @NonNull
    public Optional<String> getTarget() {
        return Optional.ofNullable(this.target);
    }

    @NonNull
    public Optional<VisitAction> getVisitAction() {
        return Optional.ofNullable(this.visitAction);
    }

    /**
     * 
     * @return Turbo Frame Builder.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @return Renders a TurboStream as a {@link Writable}
     */
    @NonNull
    public Optional<Writable> render() {
        if (template != null) {
            return writableOfTemplate(template);
        }
        return Optional.of(writer -> writer.write(renderTurboFrameOpeningTag() + renderTurboFrameClosingTag()));
    }

    @NonNull
    private Optional<Writable> writableOfTemplate(@NonNull Object template) {
        if (template instanceof CharSequence) {
            return Optional.of(out -> out.write(renderTurboFrameOpeningTag() + template + renderTurboFrameClosingTag()));
        }
        if (template instanceof Writable) {
            return Optional.of(out -> {
                out.write(renderTurboFrameOpeningTag());
                ((Writable) template).writeTo(out);
                out.write(renderTurboFrameClosingTag());
            });
        }
        return Optional.empty();
    }

    @NonNull
    private static String renderTurboFrameClosingTag() {
        return TURBO_FRAME_CLOSING_TAG;
    }

    @NonNull
    private String renderTurboFrameOpeningTag() {
        return OPEN_TAG + TURBO_FRAME_TAG
            + getIdHtmlAttribute().orElse("")
            + getSrcHtmlAttribute().orElse("")
            + getVisitActionHtmlAttribute().orElse("")
            + getLoadingHtmlAttribute().orElse("")
            + getTargetHtmlAttribute().orElse("")
            + getBusyHtmlAttribute().orElse("")
            + getDisabledHtmlAttribute().orElse("")
            + getAutoScrollHtmlAttribute().orElse("")
            + CLOSE_TAG;
    }

    @NonNull
    private Optional<String> getIdHtmlAttribute() {
        return getId()
            .map(value -> SPACE + htmlAttribute(ATTRIBUTE_ID, value));
    }

    @NonNull
    private Optional<String> getBusyHtmlAttribute() {
        return getBusy()
            .map(b -> SPACE + htmlAttribute(ATTRIBUTE_BUSY, Boolean.TRUE.equals(b) ? StringUtils.TRUE : StringUtils.FALSE));
    }

    @NonNull
    private Optional<String> getDisabledHtmlAttribute() {
        return getDisabled()
            .map(b -> SPACE + htmlAttribute(ATTRIBUTE_DISABLED, Boolean.TRUE.equals(b) ? StringUtils.TRUE : StringUtils.FALSE));
    }

    @NonNull
    private Optional<String> getAutoScrollHtmlAttribute() {
        return getAutoScroll()
            .map(b -> SPACE + htmlAttribute(ATTRIBUTE_AUTOSCROLL, Boolean.TRUE.equals(b) ? StringUtils.TRUE : StringUtils.FALSE));
    }

    @NonNull
    private Optional<String> getLoadingHtmlAttribute() {
        return getLoading()
            .map(value -> SPACE + htmlAttribute(ATTRIBUTE_LOADING, value.toString()));
    }

    @NonNull
    private Optional<String> getSrcHtmlAttribute() {
        return getSrc()
            .map(value -> SPACE + htmlAttribute(ATTRIBUTE_SRC, value));
    }

    @NonNull
    private Optional<String> getTargetHtmlAttribute() {
        return getTarget()
            .map(v -> SPACE + htmlAttribute(ATTRIBUTE_TARGET, v));
    }

    @NonNull
    private Optional<String> getVisitActionHtmlAttribute() {
        return getVisitAction()
            .map(v -> SPACE + htmlAttribute(ATTRIBUTE_DATA_TURBO_ACTION, v.toString()));
    }

    @NonNull
    private String htmlAttribute(@NonNull String key, @NonNull String value) {
        return String.join(EQUALS, key, DOUBLE_QUOTE + value + DOUBLE_QUOTE);
    }

    /**
     * Turbo Frame Builder.
     */
    public static class Builder {

        @Nullable
        private String id;

        @Nullable
        private String src;

        @Nullable
        private Loading loading;

        @Nullable
        private String target;

        @Nullable
        private Boolean busy;

        @Nullable
        private Boolean disabled;

        @Nullable
        private Boolean autoscroll;

        @Nullable
        private Object template;

        @Nullable
        private String templateView;

        @Nullable
        private Object templateModel;

        @Nullable
        private VisitAction visitAction;

        /**
         *
         * @param src src attribute
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder src(@Nullable String src) {
            this.src = src;
            return this;
        }

        /**
         *
         * @param id id attribute
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        /**
         *
         * @param target target attribute
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder target(@Nullable String target) {
            this.target = target;
            return this;
        }

        /**
         *
         * @param loading eager and lazy
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder loading(@Nullable Loading loading) {
            this.loading = loading;
            return this;
        }
        
        /**
         *
         * @param busy Busy attribute
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder busy(@Nullable Boolean busy) {
            this.busy = busy;
            return this;
        }
        
        /**
         *
         * @param disabled Disabled Attribute
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder disabled(@Nullable Boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        /**
         *
         * @param autoscroll Autoscroll Attribute
         * @return Turbo Frame Builder
         */
        @NonNull
        public Builder autoscroll(@Nullable Boolean autoscroll) {
            this.autoscroll = autoscroll;
            return this;
        }

        /**
         * Sets the template with a View and Model.
         * @param view The View name
         * @param model The Model
         * @return The Builder
         */
        @NonNull
        public Builder template(@NonNull String view, Object model) {
            this.templateView = view;
            this.templateModel = model;
            return this;
        }

        /**
         * Sets the Turbo Frame  with a String. E.g. HTML.
         * @param html The turbo frame content
         * @return The Builder
         */
        @NonNull
        public Builder template(@NonNull String html) {
            this.template = html;
            return this;
        }

        /**
         * Sets the Turbo frame content with a {@link Writable}.
         * @param writable The template as a {@link Writable}.
         * @return The Builder
         */
        @NonNull
        public Builder template(@NonNull Writable writable) {
            this.template = writable;
            return this;
        }

        /**
         * Sets the template's view name.
         * @param templateView The View name
         * @return The Builder
         */
        @NonNull
        public Builder templateView(@NonNull String templateView) {
            this.templateView = templateView;
            return this;
        }

        /**
         * Sets the template's model.
         * @param templateModel template model.
         * @return The Builder
         */
        @NonNull
        public Builder templateModel(@NonNull Object templateModel) {
            this.templateModel = templateModel;
            return this;
        }

        /**
         *
         * @param visitAction Visit Action
         * @return The builder.
         */
        @NonNull
        public Builder visitAction(@NonNull VisitAction visitAction) {
            this.visitAction = visitAction;
            return this;
        }

        /**
         *
         * @return Turbo Frame
         */
        @NonNull
        public TurboFrame build() {
            return new TurboFrame(id,
                src,
                loading,
                busy,
                disabled,
                target,
                autoscroll,
                visitAction,
                template);
        }

        /**
         *
         * @return The TurboStream template view name.
         */
        @NonNull
        public Optional<String> getTemplateView() {
            return Optional.ofNullable(templateView);
        }

        /**
         *
         * @return The TurboStream template model.
         */
        @NonNull
        public Optional<Object> getTemplateModel() {
            return Optional.ofNullable(templateModel);
        }


        /**
         * Creates a Turbo Frame builder if annotation {@link TurboFrameView} is found in the route and the request is a turbo request.
         * @param request HTTP Request
         * @param response HTTP Response
         * @return Creates a Turbo Frame builder if annotation {@link TurboFrameView} is found in the route and the request is a turbo request.
         */
        @NonNull
        public static Optional<TurboFrame.Builder> of(@NonNull HttpRequest<?> request,
                                                       @NonNull HttpResponse<?> response) {
            return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                .flatMap(routeMatch -> of(request, routeMatch));
        }

        @NonNull
        private static Optional<TurboFrame.Builder> of(@NonNull HttpRequest<?> request,
                                                        @NonNull AnnotationMetadata route) {
            Optional<String> turboFrameOptional = request.getHeaders().get(TurboHttpHeaders.TURBO_FRAME, String.class);
            Optional<String> idOptional = route.stringValue(TurboFrameView.class, MEMBER_ID);
            if (!route.hasAnnotation(TurboFrameView.class) || (!turboFrameOptional.isPresent() && route.hasAnnotation(View.class))) {
                return Optional.empty();
            }

            TurboFrame.Builder builder = TurboFrame.builder();
            route.stringValue(TurboFrameView.class).ifPresent(builder::templateView);

            route.stringValue(TurboFrameView.class, MEMBER_ACTION)
                .flatMap(VisitAction::of)
                .ifPresent(builder::visitAction);
            route.stringValue(TurboFrameView.class, MEMBER_TARGET).ifPresent(builder::target);
            idOptional.ifPresent(builder::id);
            if (!idOptional.isPresent()) {
                turboFrameOptional.ifPresent(builder::id);
            }
            route.stringValue(TurboFrameView.class, MEMBER_SRC).ifPresent(builder::src);
            route.stringValue(TurboFrameView.class, MEMBER_LOADING)
                .flatMap(Loading::of)
                .ifPresent(builder::loading);

            parseBoolean(route, MEMBER_BUSY).ifPresent(builder::busy);
            parseBoolean(route, MEMBER_DISABLED).ifPresent(builder::disabled);
            parseBoolean(route, MEMBER_AUTOSCROLL).ifPresent(builder::autoscroll);
            return Optional.of(builder);
        }

        @NonNull
        private static Optional<Boolean> parseBoolean(@NonNull AnnotationMetadata route,
                                                      @NonNull String member) {
            return route.stringValue(TurboFrameView.class, member)
                .flatMap(str -> {
                if (str.equals(StringUtils.TRUE)) {
                    return Optional.of(Boolean.TRUE);
                } else if (str.equals(StringUtils.FALSE)) {
                    return Optional.of(Boolean.FALSE);
                }
                return Optional.empty();
            });
        }
    }
}
