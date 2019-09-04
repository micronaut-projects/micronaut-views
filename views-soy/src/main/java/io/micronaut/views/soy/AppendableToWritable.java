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

import java.io.IOException;
import java.io.Writer;


/**
 * Adapts {@link Appendable} to {@link Writable} for use when rendering Soy templates.
 *
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.2.1
 */
public class AppendableToWritable implements Writable, Appendable, AdvisingAppendable {
  private final StringBuilder builder = new StringBuilder();

  @Override
  public AdvisingAppendable append(CharSequence charSequence) {
    builder.append(charSequence);
    return this;
  }

  @Override
  public AdvisingAppendable append(CharSequence charSequence, int i, int i1) {
    builder.append(charSequence, i, i1);
    return this;
  }

  @Override
  public AdvisingAppendable append(char c) {
    builder.append(c);
    return this;
  }

  @Override
  public boolean softLimitReached() {
    return false;
  }

  @Override
  public void writeTo(Writer out) throws IOException {
    out.write(builder.toString());
  }

}
