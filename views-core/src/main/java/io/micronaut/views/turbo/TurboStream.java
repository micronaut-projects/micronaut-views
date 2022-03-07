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
import io.micronaut.views.View;
import io.micronaut.views.turbo.http.TurboHttpHeaders;
import io.micronaut.views.turbo.http.TurboMediaType;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * Turbo Stream.
 * @author Sergio del Amo
 * @since 3.3.0
 */
public final class TurboStream implements Writable {

    private static final String TURBO_TEMPLATE_TAG = "template";
    private static final String TURBO_STREAM_TAG = "turbo-stream";
    private static final String TURBO_STREAM_CLOSING_TAG = "</turbo-stream>";
    private static final String TURBO_STREAM_ATTRIBUTE_TARGET = "target";
    private static final String TURBO_STREAM_ATTRIBUTE_ACTION = "action";
    private static final String TURBO_STREAM_ATTRIBUTE_TARGETS = "targets";
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

    @Nullable
    private final Object template;

    TurboStream(@NonNull TurboStreamAction action,
                @Nullable String targetDomId,
                @Nullable String targetCssQuerySelector,
                @Nullable Object template) {
        this.action = action;
        this.targetDomId = targetDomId;
        this.targetCssQuerySelector = targetCssQuerySelector;
        this.template = template;
    }

    @NonNull
    public TurboStreamAction getAction() {
        return action;
    }

    @NonNull
    public Optional<String> getTargetDomId() {
        return Optional.ofNullable(targetDomId);
    }

    @NonNull
    public Optional<String> getTargetCssQuerySelector() {
        return Optional.ofNullable(targetCssQuerySelector);
    }

    @NonNull
    public Optional<Object> getTemplate() {
        return Optional.ofNullable(template);
    }

    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        out.write(toString());
    }

    @NonNull
    public Optional<Writable> render() {
        if (getTemplate().isPresent()) {
            Object template = getTemplate().get();
            if (template instanceof CharSequence) {
                return Optional.of((writer) -> {
                    writer.write(renderTurboStreamOpeningTag() + OPEN_TAG + TURBO_TEMPLATE_TAG + CLOSE_TAG + template + OPEN_TAG + SLASH + TURBO_TEMPLATE_TAG + CLOSE_TAG + renderTurboStreamClosingTag());
                });
            } else if (template instanceof Writable) {
                return Optional.of((writer) -> {
                    writer.write(renderTurboStreamOpeningTag());
                    writer.write(OPEN_TAG + TURBO_TEMPLATE_TAG + CLOSE_TAG);
                    ((Writable) template).writeTo(writer);
                    writer.write(OPEN_TAG + SLASH + TURBO_TEMPLATE_TAG + CLOSE_TAG);
                    writer.write(renderTurboStreamClosingTag());
                });
            }
            return Optional.empty();
        } else {
            return Optional.of((writer) -> {
                writer.write(renderTurboStreamOpeningTag() + renderTurboStreamClosingTag());
            });
        }
    }

    private String renderTurboStreamClosingTag() {
        return TURBO_STREAM_CLOSING_TAG;
    }

    private String renderTurboStreamOpeningTag() {
        String html = OPEN_TAG + TURBO_STREAM_TAG + SPACE + TURBO_STREAM_ATTRIBUTE_ACTION + EQUALS + DOUBLE_QUOTE + getAction() + DOUBLE_QUOTE + SPACE;
        if (getTargetDomId().isPresent()) {
            html += TURBO_STREAM_ATTRIBUTE_TARGET + EQUALS + DOUBLE_QUOTE + getTargetDomId().get() + DOUBLE_QUOTE;
        } else if (getTargetCssQuerySelector().isPresent()) {
            html += TURBO_STREAM_ATTRIBUTE_TARGETS + EQUALS + DOUBLE_QUOTE + getTargetCssQuerySelector().get() + DOUBLE_QUOTE;
        }
        html += CLOSE_TAG;
        return html;
    }

    /**
     * Turbo Stream Builder.
     */
    public static class Builder {
        private TurboStreamAction action;
        private String targetDomId;
        private String targetCssQuerySelector;
        private Object template;
        private String templateView;
        private Object templateModel;

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
            return new TurboStream(action,
                    targetDomId,
                    targetCssQuerySelector,
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

        @NonNull
        public static Optional<TurboStream.Builder> of(@NonNull HttpRequest<?> request,
                                                       @NonNull HttpResponse<?> response) {
            Optional<AnnotationMetadata> routeMatch = response.getAttribute(HttpAttributes.ROUTE_MATCH,
                    AnnotationMetadata.class);
            if (!routeMatch.isPresent()) {
                return Optional.empty();
            }
            return of(request, routeMatch.get());
        }

        private static boolean accepts(@NonNull HttpRequest<?> request) {
            return request.getHeaders()
                    .accept()
                    .stream()
                    .anyMatch(mediaType -> mediaType.getName().contains(TurboMediaType.TURBO_STREAM));
        }

        private static Optional<TurboStream.Builder> of (@NonNull HttpRequest<?> request,
                                                         @NonNull AnnotationMetadata route) {
            Optional<String> turboFrameOptional = request.getHeaders().get(TurboHttpHeaders.TURBO_FRAME, String.class);
            if (!route.hasAnnotation(TurboView.class) ||
                    (!accepts(request) && !turboFrameOptional.isPresent() && route.hasAnnotation(View.class))) {
                return Optional.empty();
            }
            TurboStream.Builder builder = TurboStream.builder();
            route.getValue(TurboView.class, String.class).ifPresent(builder::templateView);
            route.getValue(TurboView.class, "action", TurboStreamAction.class).ifPresent(builder::action);
            route.getValue(TurboView.class, "targetDomId", String.class).ifPresent(builder::targetDomId);
            route.getValue(TurboView.class, "targetCssQuerySelector", String.class).ifPresent(builder::targetCssQuerySelector);

            if (!builder.getTargetCssQuerySelector().isPresent() &&
                    !builder.getTargetDomId().isPresent()) {
                        turboFrameOptional.ifPresent(builder::targetDomId);
            }
            return Optional.of(builder);
        }
    }

}
