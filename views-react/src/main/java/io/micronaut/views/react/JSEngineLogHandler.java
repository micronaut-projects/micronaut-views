package io.micronaut.views.react;

import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Forwards/redirects log messages from the GraalJS / Truffle engines themselves to SLF4J.
 * Note that Javascript's {@code console.log()} is handled differently.
 */
@Singleton
class JSEngineLogHandler extends Handler {
    private static final Logger logger = LoggerFactory.getLogger(ReactViewsRenderer.class);

    @Override
    public void publish(LogRecord record) {
        String message = record.getMessage();
        Throwable thrown = record.getThrown();
        String level = record.getLevel().getName();
        switch (level) {
            case "SEVERE":
                logger.error(message, thrown);
                break;
            case "WARNING":
                logger.warn(message, thrown);
                break;
            case "INFO":
                logger.info(message, thrown);
                break;
            case "CONFIG":
            case "FINE":
                logger.debug(message, thrown);
                break;
            case "FINER":
            case "FINEST":
                logger.trace(message, thrown);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + level);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
