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
package io.micronaut.views.jstachio;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import io.jstach.jstachio.JStachio;
import io.jstach.jstachio.Output.EncodedOutput;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;


class JStachioWritable implements Writable {
    private final JStachio jstachio;
    private final Object model;
    
    public JStachioWritable(JStachio jstachio, Object model) {
        super();
        this.jstachio = jstachio;
        this.model = model;
    }

    @Override
    public void writeTo(Writer out) throws IOException {
        // This is actually slower in most cases where non ascii characters are present.
        // This is because JStachio pre-encodes the static parts of the template.
        jstachio.execute(model, out);
    }
    
    @Override
    public void writeTo(OutputStream outputStream, @Nullable Charset charset) throws IOException {
        charset = charset == null ? StandardCharsets.UTF_8 : charset;
        //Should we close or flush the outputStream?
        //Or will that commit the request to early.
        var out = EncodedOutput.of(outputStream, charset);
        jstachio.write(model, out);
    }
}
