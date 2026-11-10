/*
 * Copyright 2017-2026 original authors
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
package io.micronaut.views.jte;

import gg.jte.TemplateOutput;
import gg.jte.output.Utf8ByteOutput;
import gg.jte.output.WriterOutput;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.io.Writable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * A template of development mode as a {@link Writable}: it renders through {@link SourceTemplates}, which compiles
 * the template first if it needs to be, and holds the read lock while it renders. Writes as {@link JteWritable}
 * does.
 *
 * @author graemerocher
 * @since 6.4.0
 */
@Internal
final class SourceTemplatesWritable implements Writable {
    private final SourceTemplates sourceTemplates;
    private final String viewName;
    private final Map<String, Object> data;
    private final Function<TemplateOutput, TemplateOutput> outputDecorator;

    /**
     * @param sourceTemplates The templates of development mode, which compile at runtime
     * @param viewName The template name
     * @param data The model
     * @param outputDecorator Decorates the output
     */
    SourceTemplatesWritable(SourceTemplates sourceTemplates, String viewName, Map<String, Object> data, Function<TemplateOutput, TemplateOutput> outputDecorator) {
        this.sourceTemplates = sourceTemplates;
        this.viewName = viewName;
        this.data = data;
        this.outputDecorator = outputDecorator;
    }

    private void render(TemplateOutput output) {
        sourceTemplates.render(viewName, data, outputDecorator.apply(output));
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        render(new WriterOutput(out));
    }

    @Override
    public void writeTo(OutputStream outputStream, Charset charset) throws IOException {
        if (charset == null || StandardCharsets.UTF_8.equals(charset)) {
            Utf8ByteOutput output = new Utf8ByteOutput();
            render(output);
            output.writeTo(outputStream);
        } else {
            Writable.super.writeTo(outputStream, charset);
        }
    }
}
