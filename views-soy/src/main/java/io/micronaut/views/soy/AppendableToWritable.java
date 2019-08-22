package io.micronaut.views.soy;

import com.google.template.soy.jbcsrc.api.AdvisingAppendable;
import io.micronaut.core.io.Writable;

import java.io.IOException;
import java.io.Writer;


/**
 * Adapts {@link Appendable} to {@link Writable} for use when rendering Soy templates.
 */
public class AppendableToWritable implements Writable, Appendable, AdvisingAppendable {
  private final StringBuilder builder = new StringBuilder();

  @Override
  public AdvisingAppendable append(CharSequence charSequence) throws IOException {
    builder.append(charSequence);
    return this;
  }

  @Override
  public AdvisingAppendable append(CharSequence charSequence, int i, int i1) throws IOException {
    builder.append(charSequence, i, i1);
    return this;
  }

  @Override
  public AdvisingAppendable append(char c) throws IOException {
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
