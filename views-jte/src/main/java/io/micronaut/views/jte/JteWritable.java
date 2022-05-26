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
package io.micronaut.views.jte;

import gg.jte.TemplateEngine;
import gg.jte.TemplateOutput;
import gg.jte.output.Utf8ByteOutput;
import gg.jte.output.WriterOutput;
import io.micronaut.core.io.Writable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * Turn JTE rendering logic into a Writable
 */
public class JteWritable implements Writable {
    private final TemplateEngine templateEngine;
    private final String viewName;
    private final Map<String, Object> data;
    private final Function<TemplateOutput, TemplateOutput> outputDecorator;

    public JteWritable(TemplateEngine templateEngine, String viewName, Map<String, Object> data, Function<TemplateOutput, TemplateOutput> outputDecorator) {
        this.templateEngine = templateEngine;
        this.viewName = viewName;
        this.data = data;
        this.outputDecorator = outputDecorator;
    }

    private void render(TemplateOutput output) {
        TemplateOutput decorated = outputDecorator.apply(output);
        templateEngine.render(viewName, data, decorated);
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        TemplateOutput output = new WriterOutput(out);
        render(output);
    }

    @Override
    public void writeTo(OutputStream outputStream, Charset charset) throws IOException {
        if (charset == null || StandardCharsets.UTF_8.equals(charset)) {
            // this enables "binary output" - see https://github.com/casid/jte/blob/master/DOCUMENTATION.md#binary-rendering-for-max-throughput
            Utf8ByteOutput output = new Utf8ByteOutput();
            render(output);
            output.writeTo(outputStream);
        } else {
            Writable.super.writeTo(outputStream, charset);
        }
    }
}
