/*
 * Copyright 2017-2024 original authors
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

import io.micronaut.context.event.ApplicationEvent;

/**
 * The ReactJS sources changed event.
 *
 * @author Denis Stepanov
 */
final class ReactJSSourcesChangedEvent extends ApplicationEvent {
    private final long generation;

    /**
     * Records the source generation in the event so context providers can invalidate their
     * context-bound module state without assuming that a source change permits closing a context.
     *
     * @param source The source manager that detected the change
     * @param generation The newly active source generation
     */
    public ReactJSSourcesChangedEvent(ReactJSSources source, long generation) {
        super(source);
        this.generation = generation;
    }

    /**
     * @return The source generation active after the change
     */
    long generation() {
        return generation;
    }

}
