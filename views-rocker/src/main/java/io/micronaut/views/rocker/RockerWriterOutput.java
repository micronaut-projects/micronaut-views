/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.rocker;

import com.fizzed.rocker.ContentType;
import com.fizzed.rocker.runtime.AbstractRockerOutput;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * {@link Writer} implementation of {@link com.fizzed.rocker.RockerOutput}.
 *
 * @author Sam Adams
 * @since 1.3.2
 */
public class RockerWriterOutput extends AbstractRockerOutput<RockerWriterOutput>  {
    
    private Writer writer;
    
    /**
     * Creates a new instance of the Rocker writer output.
     *
     * @param writer The writer to render the templates to
     * @param contentType The content type of the output
     * @param charsetName The charset of the output
     */
    public RockerWriterOutput(Writer writer, ContentType contentType, String charsetName) {
        super(contentType, charsetName, 0);
        this.writer = writer;
    }
    
    /**
     * Creates a new instance of the Rocker writer output.
     *
     * @param writer The writer to render the templates to
     * @param contentType The content type of the output
     * @param charset The charset of the output
     */
    public RockerWriterOutput(Writer writer, ContentType contentType, Charset charset) {
        super(contentType, charset, 0);
        this.writer = writer;
    }
    
    @Override
    public AbstractRockerOutput<RockerWriterOutput> w(String string) throws IOException {
        writer.write(string);
        return this;
    }
    
    @Override
    public AbstractRockerOutput<RockerWriterOutput> w(byte[] bytes) throws IOException {
        writer.write(new String(bytes, charset));
        return this;
    }

}
