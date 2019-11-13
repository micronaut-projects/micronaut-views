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

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.TemplateParameters;
import com.google.template.soy.jbcsrc.api.RenderResult;
import com.google.template.soy.jbcsrc.api.SoySauce;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.core.io.buffer.ByteBufferFactory;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ReactiveViewRenderer;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.csp.CspConfiguration;
import io.micronaut.views.csp.CspFilter;
import io.micronaut.views.exceptions.ViewRenderingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.reactivex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Renders views with a Soy Tofu-based engine.
 *
 * @author Sam Gammon (sam@momentum.io)
 * @since 1.2.1
 */
@Singleton
@Produces(MediaType.TEXT_HTML)
@Requires(property = SoyViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = "false")
@Requires(property = SoyViewsRendererConfigurationProperties.PREFIX + ".engine", notEquals = "tofu")
@SuppressWarnings({"WeakerAccess", "UnstableApiUsage"})
public class SoySauceViewsRenderer implements ReactiveViewRenderer {
  private static final Logger LOG = LoggerFactory.getLogger(SoySauceViewsRenderer.class);
  private static final String INJECTED_NONCE_PROPERTY = "csp_nonce";

  protected final ViewsConfiguration viewsConfiguration;
  protected final SoyViewsRendererConfigurationProperties soyMicronautConfiguration;
  protected final SoyNamingMapProvider namingMapProvider;
  protected final SoySauce soySauce;
  private final ByteBufferFactory<ByteBufAllocator, ByteBuf> bufferFactory;
  private final boolean injectNonce;

  /**
   * @param viewsConfiguration Views configuration properties.
   * @param namingMapProvider Provider for renaming maps in Soy.
   * @param cspConfiguration Content-Security-Policy configuration.
   * @param bufferFactory Factory with which to create byte buffers.
   * @param soyConfiguration   Soy configuration properties.
   */
  @Inject
  SoySauceViewsRenderer(ViewsConfiguration viewsConfiguration,
                        @Nullable SoyNamingMapProvider namingMapProvider,
                        @Nullable CspConfiguration cspConfiguration,
                        ByteBufferFactory<ByteBufAllocator, ByteBuf> bufferFactory,
                        SoyViewsRendererConfigurationProperties soyConfiguration) {
    this.bufferFactory = bufferFactory;
    this.viewsConfiguration = viewsConfiguration;
    this.soyMicronautConfiguration = soyConfiguration;
    this.injectNonce = cspConfiguration != null && cspConfiguration.isNonceEnabled();
    this.namingMapProvider = namingMapProvider;
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

  private void continueRender(@Nonnull SoySauce.WriteContinuation continuation,
                              @Nonnull SoyRender target,
                              @Nonnull Emitter<ByteBuffer> emitter) throws SoyViewException {
    try {
      target.advance(continuation);
      if (target.getRenderState() == SoyRender.State.READY) {
        LOG.debug("Render is READY to proceed. Continuing.");
        SoySauce.WriteContinuation next = continuation.continueRender();
        handleRender(next, target, emitter);
      } else {
        LOG.debug("Render is NOT READY.");
      }

    } catch (IOException ioe) {
      LOG.warn("Soy encountered IOException while rendering: '" + ioe.getMessage() + "'.");
      emitter.onError(ioe);

    }
  }

  private void emitChunk(@Nonnull SoyRender target,
                         @Nonnull Emitter<ByteBuffer> emitter) {
    LOG.debug("Render emitting chunk");
    emitter.onNext(
      target.exportChunk(bufferFactory, soyMicronautConfiguration.getChunkSize()));
  }

  private void handleRender(@Nonnull SoySauce.WriteContinuation continuation,
                            @Nonnull SoyRender target,
                            @Nonnull Emitter<ByteBuffer> emitter) throws SoyViewException {
    // Emit the next chunk and keep processing.
    emitChunk(target, emitter);
    target.advance(continuation);
    if (continuation.result().type() == RenderResult.Type.DONE) {
      LOG.debug("Finished Soy render routine. Calling `onComplete`.");
      emitter.onComplete();
    }
  }

  /**
   * @param viewName view name to be render
   * @param data     response body to render it with a view
   * @param request  HTTP request
   * @param response HTTP response object assembled so far.
   * @return A writable where the view will be written to.
   */
  @Nonnull
  @Override
  public Flowable<HttpResponse<?>> render(@Nonnull String viewName,
                                          @Nullable Object data,
                                          @Nonnull HttpRequest<?> request,
                                          @Nonnull MutableHttpResponse<?> response) {
    ArgumentUtils.requireNonNull("viewName", viewName);
    LOG.debug("Preparing render for template path '" + viewName + "'");

    if ((!(data instanceof Map) && !(data instanceof SoyData)) || data instanceof HttpResponse) {
      // we were passed something other than context data for a render operation. so, duck out gracefully.
      LOG.debug("Data was not a `Map` or `SoyData`. Returning untouched by Soy for view '" + viewName + "'.");
      return Flowable.just(response);
    }

    // begin render prep
    Map<String, Object> ijOverlay = new HashMap<>(2);
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

    // set render data, inject nonce if so directed
    renderer.setData(context);
    if (injectNonce) {
      LOG.debug("CSP nonce injection is active.");
      Optional<Object> nonceObj = request.getAttribute(CspFilter.NONCE_PROPERTY);
      if (nonceObj.isPresent()) {
        String nonceValue = ((String) nonceObj.get());
        LOG.trace("Injecting nonce value '" + nonceValue + "'.");
        ijOverlay.put(INJECTED_NONCE_PROPERTY, nonceValue);
      } else {
        LOG.warn("Failed to locate generated CSP nonce value.");
      }
    } else {
      LOG.debug("CSP nonce injection is disabled.");
    }
    renderer.setIj(ijOverlay);

    // setup renaming if so directed
    if (this.soyMicronautConfiguration.isRenamingEnabled() && this.namingMapProvider != null) {
      LOG.debug("Style renaming is enabled and there is a naming map provider.");
      SoyCssRenamingMap cssMap = this.namingMapProvider.cssRenamingMap();
      SoyIdRenamingMap idMap = this.namingMapProvider.idRenamingMap();
      if (cssMap != null) {
        LOG.trace("Soy CSS rewrite map found.");
        renderer.setCssRenamingMap(cssMap);
      }
      if (idMap != null) {
        LOG.trace("Soy ID rewrite map found.");
        renderer.setXidRenamingMap(idMap);
      }
    } else {
      LOG.debug("Style renaming is disabled.");
    }

    // prime the initial render
    return Flowable.<ByteBuffer, SoyRender>generate(SoyRender::create, (buffer, emitter) -> {
      // trigger initial render cycle, which may finish the response
      try {
        final SoySauce.WriteContinuation op = buffer.getContinuation();
        if (op == null) {
          // no continuation means we are doing the first render run, which may complete it
          LOG.trace("Initial render for view '" + viewName + "'");
          handleRender(renderer.renderHtml(buffer), buffer, emitter);
        } else {
          // otherwise, pick up where we left off
          LOG.trace("Continue render for view '" + viewName + "'");
          continueRender(op, buffer, emitter);
        }

      } catch (SoyViewException sre) {
        emitter.onError(new ViewRenderingException(
          "Soy render exception of type '" + sre.getClass().getSimpleName() +
          "' (view: '" + viewName + "'): " + sre.getMessage(), sre.getCause()));

      } catch (RuntimeException | IOException rxe) {
        emitter.onError(new ViewRenderingException(
          "Unhandled error of type '" + rxe.getClass().getSimpleName() +
          "' rendering Soy Sauce view [" + viewName + "]: " + rxe.getMessage(), rxe));

      }
    }, SoyRender::close).map(HttpResponse::ok);
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
