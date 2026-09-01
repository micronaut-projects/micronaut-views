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

import jakarta.annotation.PreDestroy;
import org.graalvm.polyglot.Context;

/**
 * A bean that handles the Javascript {@link Context} object representing a loaded execution
 * environment usable by one thread at a time.
 *
 * @param polyglotContext A single-threaded Javascript language context (global vars etc).
 */
record ReactJSContext(Context polyglotContext) implements AutoCloseable {
    @PreDestroy
    @Override
    public void close() {
        polyglotContext.close();
    }
}
