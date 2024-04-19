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
import io.micronaut.http.HttpAttributes;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.views.Renderable;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Turbo Stream.
 * <a href="https://turbo.hotwired.dev/reference/streams">Streams</a>.
 * @author Sergio del Amo
 * @since 3.3.0
 */
public final class TurboStream implements Renderable {

    private static final String MEMBER_ACTION = "action";
    private static final String MEMBER_TARGET_DOM_ID = "targetDomId";
    private static final String MEMBER_TARGET_CSS_QUERY_SELECTOR = "targetCssQuerySelector";
    private static final String MEMBER_REQUEST_ID = "requestId";
    private static final String MEMBER_CHILDREN_ONLY = "childrenOnly";
    private static final String TURBO_TEMPLATE_TAG = "template";
    private static final String TURBO_STREAM_TAG = "turbo-stream";
    private static final String TURBO_STREAM_CLOSING_TAG = "</turbo-stream>";
    private static final String TURBO_STREAM_ATTRIBUTE_TARGET = "target";
    private static final String TURBO_STREAM_ATTRIBUTE_ACTION = "action";
    private static final String TURBO_STREAM_ATTRIBUTE_TARGETS = "targets";
    private static final String TURBO_STREAM_ATTRIBUTE_REQUEST_ID = "request-id";
    private static final String TURBO_STREAM_ATTRIBUTE_CHILDREN_ONLY = "children-only";
    private static final String CLOSE_TAG = ">";
    private static final String OPEN_TAG = "<";
    private static final String SPACE = " ";
    private static final String EQUALS = "=";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String SLASH = "/";

    @NonNull
    private final TurboStreamAction action;

    /**
     * Target dom id.
     */
    @Nullable
    private final String targetDomId;

    /**
     * Target dom id.
     */
    @Nullable
    private final String targetCssQuerySelector;

    /**
     * request-id attribute, only relevant when action=refresh
     */
    @Nullable
    private final String requestId;

    /**
     * Morph only the children of the element designated by the target dom id.
     */
    private final boolean childrenOnly;

    @Nullable
    private final Object template;

    /**
     *
     * @deprecated use the constructor that takes also the {@code requestId} and
     * {@code childrenOnly} parameters instead
     */
    @Deprecated
    TurboStream(@NonNull TurboStreamAction action,
                @Nullable String targetDomId,
                @Nullable String targetCssQuerySelector,
                @Nullable Object template) {
        this.action = action;
        this.targetDomId = targetDomId;
        this.targetCssQuerySelector = targetCssQuerySelector;
        this.requestId = null;
        this.childrenOnly = false;
        this.template = template;
    }

    TurboStream(@NonNull TurboStreamAction action,
                @Nullable String targetDomId,
                @Nullable String targetCssQuerySelector,
                @Nullable String requestId,
                boolean childrenOnly,
                @Nullable Object template) {
        this.action = action;
        this.targetDomId = targetDomId;
        this.targetCssQuerySelector = targetCssQuerySelector;
        this.requestId = requestId;
        this.childrenOnly = childrenOnly;
        this.template = template;
    }

    /**
     *
     * @return Turbo Stream Action.
     */
    @NonNull
    public TurboStreamAction getAction() {
        return action;
    }

    /**
     *
     * @return Target DOM ID.
     */
    @NonNull
    public Optional<String> getTargetDomId() {
        return Optional.ofNullable(targetDomId);
    }

    /**
     *
     * @return Target CSS Selector
     */
    @NonNull
    public Optional<String> getTargetCssQuerySelector() {
        return Optional.ofNullable(targetCssQuerySelector);
    }

    /**
     *
     * @return request-id attribute, only relevant when action=refresh
     */
    public Optional<String> getRequestId() {
        return Optional.ofNullable(requestId);
    }

    /**
     *
     * @return Morph only the children of the element designated by the target dom id.
     */
    public boolean getChildrenOnly() {
        return childrenOnly;
    }

    /**
     *
     * @return Template.
     */
    @NonNull
    public Optional<Object> getTemplate() {
        return Optional.ofNullable(template);
    }

    /**
     *
     * @return Creates a TurboStream Builder.
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
    @Override
    public Optional<Writable> render() {
        if (getTemplate().isPresent()) {
            return getTemplate().flatMap(this::writableOfTemplate);
        }
        return Optional.of(writer -> writer.write(renderTurboStreamOpeningTag() + renderTurboStreamClosingTag()));
    }

    private Optional<Writable> writableOfTemplate(Object template) {
        if (template instanceof CharSequence) {
            return Optional.of(out -> out.write(renderTurboStreamOpeningTag() + htmlTag(TURBO_TEMPLATE_TAG, (CharSequence) template) + renderTurboStreamClosingTag()));
        }
        if (template instanceof Writable) {
            return Optional.of(out -> {
                out.write(renderTurboStreamOpeningTag());
                out.write(openHtmlTag(TURBO_TEMPLATE_TAG));
                ((Writable) template).writeTo(out);
                out.write(closeHtmlTag(TURBO_TEMPLATE_TAG));
                out.write(renderTurboStreamClosingTag());
            });
        }
        return Optional.empty();
    }

    private static String renderTurboStreamClosingTag() {
        return TURBO_STREAM_CLOSING_TAG;
    }

    private String renderTurboStreamOpeningTag() {
        return OPEN_TAG + TURBO_STREAM_TAG + SPACE + htmlAttribute(TURBO_STREAM_ATTRIBUTE_ACTION, getAction().toString())
                + getTargetDomIdHtmlAttribute().orElse("")
                + getTargetCssQuerySelectorHtmlAttribute().orElse("")
                + getRequestIdAttribute().orElse("")
                + getChildrenOnlyAttribute().orElse("")
                + CLOSE_TAG;
    }

    @NonNull
    private Optional<String> getTargetDomIdHtmlAttribute() {
        return getTargetDomId()
                .map(domId -> SPACE + htmlAttribute(TURBO_STREAM_ATTRIBUTE_TARGET, domId));
    }

    @NonNull
    private Optional<String> getTargetCssQuerySelectorHtmlAttribute() {
        return getTargetCssQuerySelector()
                .map(cssSelector -> SPACE + htmlAttribute(TURBO_STREAM_ATTRIBUTE_TARGETS, cssSelector));
    }

    @NonNull
    private Optional<String> getRequestIdAttribute() {
        return getRequestId()
            .map(requestId -> SPACE + htmlAttribute(TURBO_STREAM_ATTRIBUTE_REQUEST_ID, requestId));
    }

    @NonNull
    private Optional<String> getChildrenOnlyAttribute() {
        return getChildrenOnly()
            ? Optional.of(SPACE + TURBO_STREAM_ATTRIBUTE_CHILDREN_ONLY)
            : Optional.empty();
    }

    @NonNull
    private String htmlAttribute(@NonNull String key, @NonNull String value) {
        return String.join(EQUALS, key, DOUBLE_QUOTE + value + DOUBLE_QUOTE);
    }

    @NonNull
    private String htmlTag(@NonNull String tag, @NonNull CharSequence content) {
        return openHtmlTag(tag) + content + closeHtmlTag(tag);
    }

    @NonNull
    private String openHtmlTag(@NonNull String tag) {
        return OPEN_TAG + tag + CLOSE_TAG;
    }

    @NonNull
    private String closeHtmlTag(@NonNull String tag) {
        return OPEN_TAG + SLASH + tag + CLOSE_TAG;
    }

    /**
     * Turbo Stream Builder.
     */
    public static class Builder {
        private static final Pattern DEFAULT_DOM_ID_PATTERN = Pattern.compile("^[A-Za-z]+[\\w\\-\\:\\.]*$");
        private static final Pattern TARGET_CSS_QUERY_SELECTOR_PATTERN = Pattern.compile("^[\\w\\-\\:\\.]*$");
        private TurboStreamAction action;
        private String targetDomId;
        private String targetCssQuerySelector;
        private String requestId;
        private boolean childrenOnly;
        private Object template;
        private String templateView;
        private Object templateModel;

        @Nullable
        private Pattern targetDomIdPattern = DEFAULT_DOM_ID_PATTERN;

        @Nullable
        private Pattern targetCssQuerySelectorPattern = TARGET_CSS_QUERY_SELECTOR_PATTERN;

        /**
         *
         * @param pattern HTML attributes validation Pattern
         * @return The Builder
         */
        @NonNull
        public Builder targetCssQuerySelectorPattern(@Nullable Pattern pattern) {
            this.targetCssQuerySelectorPattern = pattern;
            return this;
        }

        /**
         *
         * @param pattern HTML attributes validation Pattern
         * @return The Builder
         */
        @NonNull
        public Builder targetDomIdPattern(@Nullable Pattern pattern) {
            this.targetDomIdPattern = pattern;
            return this;
        }

        /**
         *
         * @param action Sets the Turbo Stream Action
         * @return The Builder
         */
        @NonNull
        public Builder action(@NonNull TurboStreamAction action) {
            this.action = action;
            return this;
        }

        /**
         *
         * @param targetDomId Target DOM ID
         * @return The Builder
         */
        @NonNull
        public Builder targetDomId(@NonNull String targetDomId) {
            this.targetDomId = targetDomId;
            return this;
        }

        /**
         *
         * @param targetCssQuerySelector Target CSS Query Selector
         * @return The Builder
         */
        @NonNull
        public Builder targetCssQuerySelector(@NonNull String targetCssQuerySelector) {
            this.targetCssQuerySelector = targetCssQuerySelector;
            return this;
        }


        /**
         *
         * @param requestId request-id attribute, only relevant when action=refresh
         * @return The Builder
         */
        @NonNull
        public Builder requestId(@NonNull String requestId) {
            this.requestId = requestId;
            return this;
        }

        /**
         *
         * @param childrenOnly Morph only the children of the element designated by the target dom id.
         * @return The Builder
         */
        @NonNull
        public Builder childrenOnly(boolean childrenOnly) {
            this.childrenOnly = childrenOnly;
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
         * Sets the Turbo template with a String. E.g. HTML.
         * @param html The turbo template
         * @return The Builder
         */
        @NonNull
        public Builder template(@NonNull String html) {
            this.template = html;
            return this;
        }

        /**
         * Sets the Turbo template with a {@link Writable}.
         * @param writable The template as a {@link Writable}.
         * @return The Builder
         */
        @NonNull
        public Builder template(@NonNull Writable writable) {
            this.template = writable;
            return this;
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#APPEND}.
         * @return The Builder.
         */
        @NonNull
        public Builder append() {
            return action(TurboStreamAction.APPEND);
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#PREPEND}.
         * @return The Builder
         */
        @NonNull
        public Builder prepend() {
            return action(TurboStreamAction.PREPEND);
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#UPDATE}.
         * @return The Builder
         */
        @NonNull
        public Builder update() {
            return action(TurboStreamAction.UPDATE);
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#REMOVE}.
         * @return The Builder.
         */
        @NonNull
        public Builder remove() {
            return action(TurboStreamAction.REMOVE);
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#AFTER}.
         * @return The Builder.
         */
        @NonNull
        public Builder after() {
            return action(TurboStreamAction.AFTER);
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#BEFORE}.
         * @return the Builder
         */
        @NonNull
        public Builder before() {
            return action(TurboStreamAction.BEFORE);
        }

        /**
         * Sets the Turbo action as {@link TurboStreamAction#REPLACE}.
         * @return the Builder
         */
        @NonNull
        public Builder replace() {
            return action(TurboStreamAction.REPLACE);
        }

        /**
         *
         * @return Builds the {@link TurboStream}.
         */
        @NonNull
        public TurboStream build() {
            if (targetDomId != null && targetDomIdPattern != null && !targetDomIdPattern.matcher(targetDomId).matches()) {
                throw new IllegalArgumentException(targetDomId + " is not a valid attribute");
            }
            if (targetCssQuerySelector != null && targetCssQuerySelectorPattern != null && !targetCssQuerySelectorPattern.matcher(targetCssQuerySelector).matches()) {
                throw new IllegalArgumentException(targetCssQuerySelector + " is not a valid attribute");
            }
            Objects.requireNonNull(action, "action cannot be null");
            return new TurboStream(action,
                    targetDomId,
                    targetCssQuerySelector,
                    requestId,
                    childrenOnly,
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
         * @return TurboFrame Target DOM ID.
         */
        @NonNull
        public Optional<String> getTargetDomId() {
            return Optional.ofNullable(targetDomId);
        }

        /**
         * @return TurboFrame Target DOM ID.
         */
        @NonNull
        public Optional<String> getTargetCssQuerySelector() {
            return Optional.ofNullable(targetCssQuerySelector);
        }

        /**
         * Creates a Turbo Stream builder if annotation {@link TurboView} is found in the route and the request is a turbo request.
         * @param request HTTP Request
         * @param response HTTP Response
         * @return Creates a Turbo Stream builder if annotation {@link TurboView} is found in the route and the request is a turbo request.
         */
        @NonNull
        public static Optional<TurboStream.Builder> of(@NonNull HttpRequest<?> request,
                                                       @NonNull HttpResponse<?> response) {
            if (!TurboMediaType.acceptsTurboStream(request)) {
                return Optional.empty();
            }
            return response.getAttribute(HttpAttributes.ROUTE_MATCH, AnnotationMetadata.class)
                    .flatMap(routeMatch -> of(request, routeMatch));
        }

        @NonNull
        private static Optional<TurboStream.Builder> of(@NonNull HttpRequest<?> request,
                                                        @NonNull AnnotationMetadata route) {
            if (!route.hasAnnotation(TurboView.class)) {
                return Optional.empty();
            }
            TurboStream.Builder builder = TurboStream.builder();
            route.stringValue(TurboView.class).ifPresent(builder::templateView);
            route.getValue(TurboView.class, MEMBER_ACTION, TurboStreamAction.class).ifPresent(builder::action);
            route.stringValue(TurboView.class, MEMBER_TARGET_DOM_ID).ifPresent(builder::targetDomId);
            route.stringValue(TurboView.class, MEMBER_TARGET_CSS_QUERY_SELECTOR).ifPresent(builder::targetCssQuerySelector);
            route.stringValue(TurboView.class, MEMBER_REQUEST_ID).ifPresent(builder::requestId);
            route.booleanValue(TurboView.class, MEMBER_CHILDREN_ONLY).ifPresent(builder::childrenOnly);

            if (!builder.getTargetCssQuerySelector().isPresent() &&
                    !builder.getTargetDomId().isPresent()) {
                request.getHeaders().get(TurboHttpHeaders.TURBO_FRAME, String.class)
                        .ifPresent(builder::targetDomId);
            }
            return Optional.of(builder);
        }
    }
}
