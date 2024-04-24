/*
 * Copyright 2017-2020 original authors
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
