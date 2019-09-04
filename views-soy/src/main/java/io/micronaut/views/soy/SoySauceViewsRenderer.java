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

import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.TemplateParameters;
import com.google.template.soy.jbcsrc.api.RenderResult;
import com.google.template.soy.jbcsrc.api.SoySauce;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * Renders views with a Soy Tofu-based engine.
 *
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.2.1
 */
@Produces(MediaType.TEXT_HTML)
@Requires(property = SoyViewsRendererConfigurationProperties.PREFIX + ".engine", notEquals = "tofu")
@Requires(property = SoyViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = "false")
@Singleton
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class SoySauceViewsRenderer implements ViewsRenderer {

  private static final Logger LOG = LoggerFactory.getLogger(SoySauceViewsRenderer.class);

  protected final ViewsConfiguration viewsConfiguration;
  protected final SoyViewsRendererConfigurationProperties soyMicronautConfiguration;
  protected final SoySauce soySauce;

  /**
   * @param viewsConfiguration Views configuration properties.
   * @param soyConfiguration   Soy configuration properties.
   */
  @Inject
  SoySauceViewsRenderer(ViewsConfiguration viewsConfiguration,
                        SoyViewsRendererConfigurationProperties soyConfiguration) {
    this.viewsConfiguration = viewsConfiguration;
    this.soyMicronautConfiguration = soyConfiguration;
    final SoySauce precompiled = soyConfiguration.getCompiledTemplates();
    if (precompiled != null) {
      this.soySauce = precompiled;
    } else {
      LOG.warn("Compiling Soy templates (this may take a moment)...");
      this.soySauce = soyConfiguration.getFileSet().compileTemplates();
    }
  }

  /**
   * @param viewName view name to be render
   * @param data     response body to render it with a view
   * @return A writable where the view will be written to.
   */
  @Nonnull
  @Override
  public Writable render(@Nonnull String viewName, @Nullable Object data) {
    ArgumentUtils.requireNonNull("viewName", viewName);

    Map<String, Object> context = modelOf(data);
    final SoySauce.Renderer renderer = soySauce.newRenderer(new TemplateParameters() {
      @Override
      public String getTemplateName() {
        return viewName;
      }

      @Override
      public Map<String, SoyValueProvider> getParamsAsMap() {
        return null;
      }
    });
    renderer.setData(context);

    try {
      final AppendableToWritable target = new AppendableToWritable();
      SoySauce.WriteContinuation state;

      state = renderer.renderHtml(target);

      while (state.result().type() != RenderResult.Type.DONE) {
        switch (state.result().type()) {
          // If it's done, do nothing.
          case DONE: break;

          // Render engine is signalling that we are waiting on an async task.
          case DETACH:
            state.result().future().get();
            state = state.continueRender();
            break;

          // Output buffer is full.
          case LIMITED: break;

          default: break;
        }
      }
      return target;

    } catch (IOException e) {
      throw new ViewRenderingException(
        "Error rendering Soy Sauce view [" + viewName + "]: " + e.getMessage(), e);
    } catch (InterruptedException ixe) {
      throw new ViewRenderingException(
        "Interrupted while rendering Soy Sauce view [" + viewName + "]: " + ixe.getMessage(), ixe);
    } catch (ExecutionException exe) {
      throw new ViewRenderingException(
        "Execution error while rendering Soy Sauce view [" + viewName + "]: " + exe.getMessage(), exe);
    }
  }

  /**
   * @param view view name to be rendered
   * @return true if a template can be found for the supplied view name.
   */
  @Override
  public boolean exists(@Nonnull String view) {
    return soySauce.hasTemplate(view);
  }

}
