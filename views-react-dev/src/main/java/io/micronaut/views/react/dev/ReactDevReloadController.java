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
package io.micronaut.views.react.dev;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.sse.Event;
import org.reactivestreams.Publisher;

/**
 * Reports a rebuild over server-sent events.
 */
@Requires(bean = ReactDevConfiguration.class)
@Controller("${" + ReactDevConfiguration.PREFIX + ".path:" + ReactDevConfiguration.DEFAULT_PATH + "}")
final class ReactDevReloadController {
    private final ReactDevReloadBroadcaster broadcaster;

    ReactDevReloadController(ReactDevReloadBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    /**
     * @return a stream carrying one event per rebuild of the server bundle.
     */
    @Get(produces = MediaType.TEXT_EVENT_STREAM)
    Publisher<Event<String>> stream() {
        return broadcaster.rebuilds().map(at -> Event.of(at).name("reload"));
    }
}
