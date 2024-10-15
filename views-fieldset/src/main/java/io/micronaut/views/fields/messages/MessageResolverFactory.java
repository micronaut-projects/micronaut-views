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
package io.micronaut.views.fields.messages;

import io.micronaut.context.LocalizedMessageSource;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.Internal;
import jakarta.inject.Singleton;

@Factory
@Internal
final class MessageResolverFactory {

    @Singleton
    MessageResolver createMessageResolver(MessageSource messageSource) {
        return new MessageResolver(messageSource);
    }

    @Requires(beans = LocalizedMessageSource.class)
    @Singleton
    LocalizedMessageResolver createMessageResolver(LocalizedMessageSource messageSource) {
        return new LocalizedMessageResolver(messageSource);
    }
}
