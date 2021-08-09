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
package io.micronaut.views.soy;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyTemplate;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.jbcsrc.api.RenderResult;
import com.google.template.soy.jbcsrc.api.SoySauce;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.csp.CspConfiguration;
import io.micronaut.views.csp.CspFilter;
import io.micronaut.views.exceptions.ViewRenderingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


/**
 * Renders views with a Soy engine.
 *
 * @author Sam Gammon (sam@bloombox.io)
 * @since 1.2.1
 */
@Produces(MediaType.TEXT_HTML)
@Requires(classes = SoySauce.class)
@Singleton
@SuppressWarnings({"WeakerAccess", "UnstableApiUsage"})
public class SoySauceViewsRenderer implements ViewsRenderer {

  private static final Logger LOG = LoggerFactory.getLogger(SoySauceViewsRenderer.class);
  private static final String INJECTED_NONCE_PROPERTY = "csp_nonce";

  protected final ViewsConfiguration viewsConfiguration;
  protected final SoyViewsRendererConfigurationProperties soyMicronautConfiguration;
  protected final SoyNamingMapProvider namingMapProvider;
  protected final SoySauce soySauce;
  private final boolean injectNonce;

  /**
   * @param viewsConfiguration Views configuration properties.
   * @param cspConfiguration Content-Security-Policy configuration.
   * @param namingMapProvider Soy naming map provider
   * @param soyConfiguration   Soy configuration properties.
   */
  @Inject
  public SoySauceViewsRenderer(ViewsConfiguration viewsConfiguration,
                               @Nullable CspConfiguration cspConfiguration,
                               @Nullable SoyNamingMapProvider namingMapProvider,
                               SoyViewsRendererConfigurationProperties soyConfiguration) {
    this.viewsConfiguration = viewsConfiguration;
    this.soyMicronautConfiguration = soyConfiguration;
    this.namingMapProvider = namingMapProvider;
    this.injectNonce = cspConfiguration != null && cspConfiguration.isNonceEnabled();
    final SoySauce precompiled = soyConfiguration.getCompiledTemplates();
    if (precompiled != null) {
      this.soySauce = precompiled;
    } else {
      LOG.warn("Compiling Soy templates (this may take a moment)...");
      SoyFileSet fileSet = soyConfiguration.getFileSet();
      if (fileSet == null) {
        throw new IllegalStateException(
          "Unable to load Soy templates: no file set, no compiled templates provided.");
      }
      this.soySauce = soyConfiguration.getFileSet().compileTemplates();
    }
  }

  /**
   * @param viewName view name to be rendered
   * @param data     response body to render it with a view
   * @return A writable where the view will be written to.
   */
  @NonNull
  public Writable render(@NonNull String viewName, @Nullable Object data) {
    return render(viewName, data, null);
  }

  /**
   * @param viewName view name to be rendered
   * @param data     response body to render it with a view
   * @param request  HTTP request
   * @return A writable where the view will be written to.
   */
  @NonNull
  @Override
  public Writable render(@NonNull String viewName, @Nullable Object data, @NonNull HttpRequest<?> request) {
    ArgumentUtils.requireNonNull("viewName", viewName);

    Map<String, Object> ijOverlay = new HashMap<>(1);
    Map<String, Object> context = modelOf(data);
    final SoySauce.Renderer renderer = soySauce.newRenderer(new SoyTemplate() {
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
    if (injectNonce && request != null) {
      Optional<Object> nonceObj = request.getAttribute(CspFilter.NONCE_PROPERTY);
      if (nonceObj.isPresent()) {
        String nonceValue = ((String) nonceObj.get());
        ijOverlay.put(INJECTED_NONCE_PROPERTY, nonceValue);
      }
    }
    renderer.setIj(ijOverlay);

    if (this.soyMicronautConfiguration.isRenamingEnabled() && this.namingMapProvider != null) {
      SoyCssRenamingMap cssMap = this.namingMapProvider.cssRenamingMap();
      SoyIdRenamingMap idMap = this.namingMapProvider.idRenamingMap();
      if (cssMap != null) {
        renderer.setCssRenamingMap(cssMap);
      }
      if (idMap != null) {
        renderer.setXidRenamingMap(idMap);
      }
    }

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
  public boolean exists(@NonNull String view) {
    return soySauce.hasTemplate(view);
  }

}
