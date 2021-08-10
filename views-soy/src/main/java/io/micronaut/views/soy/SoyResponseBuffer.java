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
package io.micronaut.views.soy;

import com.google.template.soy.jbcsrc.api.AdvisingAppendable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Implements a response buffer for Soy-rendered content, that complies with {@link AdvisingAppendable}. Backed by a
 * Netty {@link ByteBuf}, acquired by default from the {@link PooledByteBufAllocator}. Once content in the buffer
 * reaches {@link #softLimit}, the method {@link #softLimitReached()} begins returning `true` to signal back-pressure
 * to the rest of the app.
 *
 * <p>`SoyResponseBuffer` also complies with {@link Closeable} and {@link AutoCloseable}, so it can be used in
 * {@code try}-with-resources blocks. Upon "closing" the response buffer, the internal buffer is released back to the
 * buffer pool.</p>
 *
 * @author Sam Gammon (sam@momentum.io)
 * @since 1.3.2
 */
@SuppressWarnings("unused")
public class SoyResponseBuffer implements Closeable, AutoCloseable, AdvisingAppendable {
    private static final Logger LOG = LoggerFactory.getLogger(SoyResponseBuffer.class);
    private static final int MAX_BUFFER_CHUNKS = 2048;
    private static final int DEFAULT_INITIAL_BUFFER = 1024;
    static final int MAX_CHUNK_SIZE = DEFAULT_INITIAL_BUFFER * 2;
    private static final float DEFAULT_SOFT_LIMIT = 0.8f;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ByteBufAllocator ALLOCATOR = PooledByteBufAllocator.DEFAULT;

    private ByteBuf chunk;
    private final CompositeByteBuf buffer;
    private final Charset charset;
    private final float softLimit;
    private final int initialBufferSize;

    // -- Constructors -- //

    /**
     * Construct an `SoyResponseBuffer` backed by a buffer of default size, with the default charset.
     */
    SoyResponseBuffer() {
        this(DEFAULT_CHARSET, DEFAULT_INITIAL_BUFFER, DEFAULT_SOFT_LIMIT);
    }

    /**
     * Construct an `SoyResponseBuffer` backed by a buffer of default size, with the specified charset.
     *
     * @param charset Character set to use.
     */
    public SoyResponseBuffer(Charset charset) {
        this(charset, DEFAULT_INITIAL_BUFFER);
    }

    /**
     * Construct an `SoyResponseBuffer` backed by a buffer of a custom size and charset.
     *
     * @param charset Charset to use.
     * @param initialBufferSize Initial buffer size to use.
     */
    private SoyResponseBuffer(Charset charset, int initialBufferSize) {
        this(charset, initialBufferSize, DEFAULT_SOFT_LIMIT);
    }

    /**
     * Construct an `SoyResponseBuffer` backed by a buffer of a custom size and charset.
     *
     * @param charset Charset to use.
     * @param initialBufferSize Buffer size to use.
     * @param softLimitRatio Ratio of buffer fill at which to begin reporting `true` for {@link #softLimitReached()}.
     */
    private SoyResponseBuffer(Charset charset, int initialBufferSize, float softLimitRatio) {
        if (softLimitRatio > 1.0 || softLimitRatio < 0.0) {
            throw new IllegalArgumentException(
                    "Cannot create `SoyResponseBuffer` with soft limit ratio of: '" + softLimitRatio + "'.");
        }
        this.charset = charset;
        this.softLimit = softLimitRatio;
        this.initialBufferSize = initialBufferSize;
        this.buffer = ALLOCATOR.compositeDirectBuffer(MAX_BUFFER_CHUNKS);
        this.chunk = allocateChunk();
    }

    /**
     * Allocate a chunk of buffer space.
     *
     * @return Byte buffer.
     */
    private ByteBuf allocateChunk() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Allocating chunk of sizing = " + initialBufferSize);
        }
        return ALLOCATOR.directBuffer(initialBufferSize);
    }

    /** Reset the internal state of the buffer. */
    private void reset() {
        chunk.clear();
        buffer.clear();
    }

    /**
     * Provide a read-only window into the currently-readable bytes in the backing {@link ByteBuf}.
     *
     * @return Derived {@link ByteBuf}, in read-only mode, that observes the portion of this response buffer's readable
     *         bytes, that were available when this method was called.
     */
    ByteBuf exportChunk() {
        int availableBytes = chunk.readableBytes();
        buffer.addComponent(true, chunk);
        chunk = allocateChunk();
        return buffer.readSlice(availableBytes).asReadOnly().retain();
    }

    /**
     * Provide a read-only window into the currently-readable bytes in the backing {@link ByteBuf}.
     *
     * @param maxBytes Maximum count of bytes to export in this chunk.
     * @return Derived {@link ByteBuf}, in read-only mode, that observes the portion of this response buffer's readable
     *         bytes, that were available when this method was called.
     */
    ByteBuf exportChunk(int maxBytes) {
        int availableBytes = chunk.readableBytes();
        buffer.addComponent(true, chunk);
        chunk = allocateChunk();
        return buffer.readSlice(Math.min(maxBytes, buffer.readableBytes())).asReadOnly().retain();
    }

    @Override
    public AdvisingAppendable append(CharSequence charSequence) {
        int size = charSequence.length();
        int available = (chunk.capacity() - chunk.writerIndex() - 1);
        int balance = available - size;
        if (balance < 0) {
            buffer.addComponent(true, chunk);
            chunk = allocateChunk();
        }

        // we can write it, there is either enough space or we made enough space
        chunk.ensureWritable(size);
        chunk.writeCharSequence(charSequence, charset);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Appended char seq of size = " + charSequence.length() +
                    " (c: " + chunk.writerIndex() + ", b: " + buffer.writerIndex() + ")");
        }
        return this;
    }

    @Override
    public AdvisingAppendable append(CharSequence csq, int start, int end) {
        return append(csq.subSequence(start, end));
    }

    @Override
    public AdvisingAppendable append(char c) {
        char[] item = {c};
        append(CharBuffer.wrap(item));
        return this;
    }

    @Override
    public boolean softLimitReached() {
        // have we met, or exceeded, soft-limit ratio of the buffer capacity?
        return (buffer.writerIndex() >= MAX_CHUNK_SIZE * softLimit);
    }

    @Override
    public void close() {
        reset();
        chunk.release();
        buffer.release();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing render buffer (state: CLOSED)");
            if (LOG.isTraceEnabled()) {
                LOG.debug("Chunk references = " + chunk.refCnt());
                LOG.debug("Buffer references = " + buffer.refCnt());
            }
        }
        chunk = null;
    }
}
