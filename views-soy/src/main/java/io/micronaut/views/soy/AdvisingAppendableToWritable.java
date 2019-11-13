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
import io.micronaut.core.io.Writable;


/**
 * Combines the interfaces necessary to adhere to both the {@link AdvisingAppendable} surface (used in new-style Soy
 * rendering, which is async/reactive), and {@link Writable}, which is used in the old-style Soy rendering.
 */
public interface AdvisingAppendableToWritable extends AdvisingAppendable, Writable {
  /* no-op */
}
