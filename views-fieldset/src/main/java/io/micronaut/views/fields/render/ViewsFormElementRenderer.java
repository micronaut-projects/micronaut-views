/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.fields.render;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.io.Writable;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import io.micronaut.views.fields.FormElement;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * Implementation of {@link FormElementRenderer} which uses {@link ViewsRenderer} to render a form element with a view template.
 *
 * @param <T> form element
 * @author Sergio del Amo
 * @since 4.1.0
 */
public class ViewsFormElementRenderer<T extends FormElement> implements FormElementRenderer<T> {

    protected final ViewsRenderer<Map<String, FormElement>, ?> viewsRenderer;
    protected final String viewName;
    protected final String modelKey;

    public ViewsFormElementRenderer(ViewsRenderer<Map<String, FormElement>, ?> viewsRenderer,
                                    String viewName,
                                    String modelKey) {
        this.viewsRenderer = viewsRenderer;
        this.viewName = viewName;
        this.modelKey = modelKey;
    }

    public ViewsFormElementRenderer(ViewsRenderer<Map<String, FormElement>, ?> viewsRenderer,
                                    String viewName) {
        this(viewsRenderer, viewName, "el");
    }

    @Override
    public String render(T formElement, Locale locale) {
        try {
            return toString(viewsRenderer.render(viewName, Collections.singletonMap(modelKey, formElement), null));
        } catch (IOException e) {
            throw new ViewRenderingException("Could not render view " + viewName + "for form element", e);
        }
    }

    @NonNull
    private static String toString(@NonNull Writable writable) throws IOException {
        StringWriter stringWriter = new StringWriter();
        writable.writeTo(stringWriter);
        return stringWriter.toString();
    }
}
