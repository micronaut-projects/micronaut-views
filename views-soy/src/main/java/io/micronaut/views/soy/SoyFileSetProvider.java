package io.micronaut.views.soy;


import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jbcsrc.api.SoySauce;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


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

  /**
   * @return Compiled set of Soy templates, if supported
   */
  default @Nullable SoySauce provideCompiledTemplates() {
    return null;
  }
}
