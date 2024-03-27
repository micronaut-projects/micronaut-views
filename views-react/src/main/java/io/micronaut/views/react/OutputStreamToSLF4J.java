package io.micronaut.views.react;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * An output stream that looks for line separators and then writes out the lines of text to the given logger.
 */
class OutputStreamToSLF4J extends OutputStream {
    private final Charset charset;

    private ByteBuffer buffer = ByteBuffer.allocate(512);

    private final LoggingEventBuilder loggingEventBuilder;

    /**
     * Creates a logging stream with the JVM's default character set.
     */
    public OutputStreamToSLF4J(LoggingEventBuilder loggingEventBuilder) {
        this(loggingEventBuilder, Charset.defaultCharset());
    }

    /**
     * Creates a logging stream with the given character set.
     */
    public OutputStreamToSLF4J(LoggingEventBuilder loggingEventBuilder, Charset charset) {
        this.loggingEventBuilder = loggingEventBuilder;
        this.charset = charset;
    }

    /**
     * Creates a logging stream for the given logger and logging level.
     */
    public OutputStreamToSLF4J(Logger logger, Level level) {
        this(logger.makeLoggingEventBuilder(level));
    }

    @Override
    public void write(int b) throws IOException {
        maybeResizeBuffer(1);
        buffer.put((byte) b);
    }

    @Override
    public void write(@NotNull byte[] b) throws IOException {
        maybeResizeBuffer(b.length);
        buffer.put(b);
    }

    private void maybeResizeBuffer(int forAdditional) {
        if (buffer.remaining() >= forAdditional)
            return;

        // Otherwise increase the buffer size by 1kb each time until it's big enough.
        var targetSize = buffer.position() + forAdditional;
        var newSize = buffer.capacity();
        assert newSize < targetSize;
        while (newSize < targetSize)
            newSize += 1024;

        var old = buffer;
        buffer = ByteBuffer.allocate(newSize);
        buffer.put(old.flip());
    }

    @Override
    public void flush() throws IOException {
        buffer.flip();
        var lines = charset.decode(buffer).toString().split("\n");
        for (String line : lines)
            loggingEventBuilder.log(line);
        buffer.clear();
    }
}
