package io.micronaut.views.soy;


import com.google.template.soy.SoyFileSet;

import javax.annotation.Nonnull;


/**
 * Interface via DI to acquire a {@link SoyFileSet}.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface SoyFileSetProvider {
  /**
   * @return Soy file set to render templates with
   */
  @Nonnull SoyFileSet provideSoyFileSet();
}
