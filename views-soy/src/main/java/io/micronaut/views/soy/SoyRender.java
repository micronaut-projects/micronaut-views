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
import com.google.template.soy.jbcsrc.api.RenderResult;
import com.google.template.soy.jbcsrc.api.SoySauce;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.core.io.buffer.ByteBufferFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.*;
import java.security.MessageDigest;
import java.util.concurrent.*;

/**
 * Describes an individual render routine via SoySauce, with a continuation, context,
 * and a response buffer from Soy.
 *
 * @author Sam Gammon (sam@momentum.io)
 * @since 1.3.2
 */
@Immutable
@SuppressWarnings("unused")
public final class SoyRender implements Closeable, AutoCloseable, AdvisingAppendable {
    private static final Logger LOG = LoggerFactory.getLogger(SoyRender.class);
    private static final Long FUTURE_TIMEOUT = 60L;
    private static final int CHUNK_BUFFER_SIZE = 4096 * 2;

    /** Defines states that the {@link SoyRender} wrapper inhabits. */
    public enum State {
        /** The renderer is ready to render. */
        READY,

        /** The renderer is waiting for the buffer to catch up. */
        FLUSH,

        /** The renderer is waiting on some future value. */
        WAITING,

        /** The renderer is done and can be cleaned up. */
        DONE,

        /** The buffer is closed and the renderer is fully done. */
        CLOSED
    }

    // -- Internals -- //
    private @NonNull State renderState;
    private @Nullable Future blocker;
    private @Nullable SoySauce.WriteContinuation continuation;
    private @NonNull final SoyResponseBuffer soyBuffer;

    /**
     * Initial constructor: an empty Soy render.
     *
     * @param continuation Initial continuation for the underlying renderer.
     */
    private SoyRender(@Nullable SoySauce.WriteContinuation continuation) {
        this.blocker = null;
        this.renderState = State.READY;
        this.soyBuffer = new SoyResponseBuffer();
        this.continuation = continuation;
    }

    /**
     * Create an initial state object for a Soy render operation.
     *
     * @return Empty Soy render state object.
     */
    public static SoyRender create() {
        return new SoyRender(null);
    }

    // -- Getters -- //
    /**
     * @return Current state of this render.
     */
    @NonNull State getRenderState() {
        return renderState;
    }

    /**
     * @return Current Soy response buffer.
     */
    public @NonNull SoyResponseBuffer getSoyBuffer() {
        return soyBuffer;
    }

    /**
     * @return Current Soy continuation.
     */
    @Nullable SoySauce.WriteContinuation getContinuation() {
        return continuation;
    }

    /**
     * @return Get the future currently blocking render.
     */
    @Nullable Future getBlocker() {
        return blocker;
    }

    // -- Advising Appendable -- //
    /**
     * Append a character sequence to the underlying render buffer.
     *
     * @param csq Character sequence to append.
     * @return Self, for chain-ability or immutability.
     * @throws IOException If the buffer is already closed.
     */
    @Override
    public AdvisingAppendable append(CharSequence csq) throws IOException {
        if (this.renderState == State.CLOSED) {
            throw new IOException("Cannot append to closed render buffer.");
        }
        return this.soyBuffer.append(csq);
    }

    /**
     * Append some character sequence to the underlying render buffer,
     * slicing the sequence from `start` to `end`.
     *
     * @param csq Character sequence to slice and append.
     * @param start Start of the sequence slice.
     * @param end End of the sequence slice.
     * @return Self, for chain-ability or immutability.
     * @throws IOException If the buffer is already closed.
     */
    @Override
    public AdvisingAppendable append(CharSequence csq, int start, int end) throws IOException {
        if (this.renderState == State.CLOSED) {
            throw new IOException("Cannot append to closed render buffer.");
        }
        return this.soyBuffer.append(csq, start, end);
    }

    /**
     * Append a single character to the underlying render buffer.
     *
     * @param c Single character to append.
     * @return Self, for chain-ability or immutability.
     * @throws IOException If the buffer is already closed.
     */
    @Override
    public AdvisingAppendable append(char c) throws IOException {
        if (this.renderState == State.CLOSED) {
            throw new IOException("Cannot append to closed render buffer.");
        }
        return this.soyBuffer.append(c);
    }

    /**
     * Indicates that an internal limit has been reached or exceeded and that write operations should
     * be suspended soon.
     */
    @Override
    public boolean softLimitReached() {
        boolean limit = this.soyBuffer.softLimitReached();
        if (LOG.isDebugEnabled() && limit) {
            LOG.debug("Soft limit reached!");
        } else if (LOG.isTraceEnabled()) {
            LOG.trace("Soft limit not yet reached");
        }
        return limit;
    }


    // -- Methods -- //
    /**
     * Close the underlying render operation and cleanup any resources.
     *
     * @throws IOException If the buffer is already closed.
     */
    @Override
    public void close() throws IOException {
        if (this.renderState == State.CLOSED) {
            throw new IOException("Cannot close an already-closed render buffer.");
        }
        LOG.debug("Closing render buffer (state: CLOSED)");
        this.renderState = State.CLOSED;
        this.soyBuffer.close();
    }

    /**
     * Export a rendered chunk of raw bytes, in a buffer, from the Soy response
     * buffer held internally.
     *
     * @param factory Factory with which to create byte buffers.
     * @param digester Digester to factor the chunk into, if applicable.
     * @return Exported chunk from the underlying buffer.
     */
    @NonNull ByteBuffer exportChunk(@NonNull ByteBufferFactory<ByteBufAllocator, ByteBuf> factory,
                                    @Nullable MessageDigest digester) {
        ByteBuf buf = soyBuffer.exportChunk();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exporting full chunk: " + buf.toString());
        }
        if (digester != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mixing chunk into non-null MessageDigest.");
                digester.update(buf.array());
            }
        }
        return factory.wrap(buf);
    }

    /**
     * Export a rendered chunk of raw bytes, in a buffer, from the Soy response
     * buffer held internally. This method additionally allows a chunk size.
     *
     * @param factory Factory with which to create byte buffers.
     * @param digester Digester to factor the chunk into, if applicable.
     * @param maxSize Maximum chunk size to specify.
     * @return Exported chunk from the underlying buffer.
     */
    @NonNull ByteBuffer exportChunk(@NonNull ByteBufferFactory<ByteBufAllocator, ByteBuf> factory,
                                    @Nullable MessageDigest digester,
                                    int maxSize) {
        ByteBuf buf = soyBuffer.exportChunk(maxSize);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exporting capped chunk (max-size: " + maxSize + "): " + buf.toString());
        }
        if (digester != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mixing chunk into non-null MessageDigest.");
                digester.update(buf.array());
            }
        }
        return factory.wrap(buf);
    }

    /**
     * Advance the render routine, producing a new render state object.
     *
     * @param op Continuation for the render.
     * @throws SoyViewException If some error occurs while rendering. The inner exception will be set as the cause.
     *         Causes include: {@link IOException} if the buffer is already closed or the template cannot be found,
     *         {@link ExecutionException}/{@link TimeoutException}/{@link InterruptedException} if a render-blocking
     *         future doesn't finish in time or is interrupted or fails, and runtime exceptions from Soy templates
     *         (like {@link NullPointerException}, {@link IllegalArgumentException} and so on).
     */
    void advance(@NonNull SoySauce.WriteContinuation op) throws SoyViewException {
        this.continuation = op;
        try {
            // resume with the continuation we were given, or the one we have.
            RenderResult.Type result = op.result().type();
            switch (result) {
                case DONE:
                    if (this.renderState != State.DONE) {
                        LOG.debug("SoyRender flow is DONE.");
                        this.renderState = State.DONE;
                    }
                    break;

                case LIMITED:
                    LOG.debug("SoyRender received LIMITED signal.");
                    if (this.renderState == State.FLUSH) {
                        // we are resuming
                        LOG.trace("Determined to be resuming (READY).");
                        this.renderState = State.READY;
                    } else {
                        // we are waiting
                        LOG.trace("Determined to be switching away to FLUSH.");
                        this.renderState = State.FLUSH;
                    }
                    break;

                case DETACH:
                    LOG.debug("SoyRender received DETACH signal.");
                    if (this.renderState == State.WAITING) {
                        LOG.trace("Still waiting on future value");

                        // they are telling us the future should be done now.
                        if (this.blocker == null) {
                            throw new NullPointerException("Cannot resume null future.");
                        }
                        if (!this.blocker.isDone()) {
                            LOG.trace("Future value is not yet ready. Blocking for return.");
                            this.blocker.get(FUTURE_TIMEOUT, TimeUnit.SECONDS);
                            LOG.trace("Future value block finished.");
                        }

                        // future is done. do the next render.
                        this.blocker = null;
                        this.renderState = State.READY;
                        LOG.trace("Future value has arrived: SoyRender is READY.");

                    } else {
                        // we are detaching to handle a future value.
                        this.renderState = State.WAITING;
                        this.blocker = op.result().future();
                    }
                    break;

                default:
                    LOG.warn("Unhandled render signal: '" + result == null ? "null" : result.name() + "'.");
                    break;
            }

        } catch (RuntimeException | ExecutionException | TimeoutException | InterruptedException rxe) {
            throw new SoyViewException(rxe);

        }
    }
}
