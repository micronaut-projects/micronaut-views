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
import org.graalvm.polyglot.Value;

import java.util.List;

/**
 * A bean that handles the Javascript {@link Context} object representing a loaded execution
 * environment usable by one thread at a time.
 *
 * @param polyglotContext A single-threaded Javascript language context (global vars etc).
 * @param render The {@code ssr} function defined in Javascript.
 * @param ssrModule The JS module containing the user's React components.
 */
record ReactJSContext(Context polyglotContext,
                      Value render,
                      Value ssrModule) implements AutoCloseable {
    // Symbols the user's server side bundle might supply us with.
    private static final List<String> IMPORT_SYMBOLS = List.of("React", "ReactDOMServer", "renderToString", "h");

    boolean moduleHasMember(String memberName) {
        assert !IMPORT_SYMBOLS.contains(memberName) : "Should not query the server-side bundle for member name " + memberName;
        return ssrModule.hasMember(memberName);
    }

    @PreDestroy
    @Override
    public void close() {
        polyglotContext.close();
    }
}
