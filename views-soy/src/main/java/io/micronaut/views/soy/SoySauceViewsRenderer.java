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
import com.google.template.soy.data.SoyTemplate;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.jbcsrc.api.RenderResult;
import com.google.template.soy.jbcsrc.api.SoySauce;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.beans.BeanMap;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.core.io.buffer.ByteBufferFactory;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.HttpRequest;
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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * Renders views with a Soy Sauce-based engine.
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
    private static final String SOY_CONTEXT_SENTINEL = "__soy_context__";
    private static final String ETAG_HASH_ALGORITHM = "MD5";
    private static final Boolean EMIT_ONE_CHUNK = true;

    protected final ViewsConfiguration viewsConfiguration;
    protected final SoyViewsRendererConfigurationProperties soyMicronautConfiguration;
    protected final SoyNamingMapProvider namingMapProvider;
    protected final SoySauce soySauce;
    private final ByteBufferFactory<ByteBufAllocator, ByteBuf> bufferFactory;
    private final boolean injectNonce;
    private volatile boolean digesterActive = false;

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
        this.soySauce = precompiled != null ? precompiled : precompileTemplates(soyConfiguration);
    }

    /**
     * Precompile templates into a {@link SoySauce} instance.
     *
     * @param soyConfiguration Configuration for Soy.
     * @return Precompiled templates.
     */
    private static SoySauce precompileTemplates(SoyViewsRendererConfigurationProperties soyConfiguration) {
        LOG.warn("Compiling Soy templates (this may take a moment)...");
        SoyFileSet fileSet = soyConfiguration.getFileSet();
        if (fileSet == null) {
            throw new IllegalStateException(
                    "Unable to load Soy templates: no file set, no compiled templates provided.");
        }
        return soyConfiguration.getFileSet().compileTemplates();
    }

    private void continueRender(@NonNull SoySauce.WriteContinuation continuation,
                                @NonNull SoyRender target,
                                @NonNull Emitter<ByteBuffer> emitter,
                                @Nullable MessageDigest digester) throws SoyViewException {
        try {
            target.advance(continuation);
            if (target.getRenderState() == SoyRender.State.READY) {
                LOG.debug("Render is READY to proceed. Continuing.");
                SoySauce.WriteContinuation next = continuation.continueRender();
                handleRender(next, target, emitter, digester);
            } else {
                LOG.debug("Render is NOT READY.");
            }

        } catch (IOException ioe) {
            LOG.warn("Soy encountered IOException while rendering: '" + ioe.getMessage() + "'.");
            emitter.onError(ioe);

        }
    }

    private void emitChunk(@NonNull SoyRender target,
                           @NonNull Emitter<ByteBuffer> emitter,
                           @Nullable MessageDigest digester) {
        LOG.debug("Render emitting chunk");
        emitter.onNext(
                target.exportChunk(
                        bufferFactory,
                        this.digesterActive ? digester : null,
                        soyMicronautConfiguration.getChunkSize()));
    }

    private void handleRender(@NonNull SoySauce.WriteContinuation continuation,
                              @NonNull SoyRender target,
                              @NonNull Emitter<ByteBuffer> emitter,
                              @Nullable MessageDigest digester) throws SoyViewException {
        // Emit the next chunk and keep processing.
        if (!EMIT_ONE_CHUNK) {
            emitChunk(target, emitter, digester);
            this.digesterActive = false;
        }
        target.advance(continuation);
        if (continuation.result().type() == RenderResult.Type.DONE) {
            LOG.debug("Finished Soy render routine. Calling `onComplete`.");
            if (EMIT_ONE_CHUNK) {
                emitChunk(target, emitter, digester);
                this.digesterActive = false;
            }
            emitter.onComplete();
        }
    }

    /**
     * Package the {@link SoyContextMediator} in a singleton map if we are handed one directly. If not, fallback to the
     * default functionality, which passes through Map instances, and then falls back to generating Bean maps.
     *
     * @param data The data to render a Soy view with.
     * @return Packaged or converted model data to render the Soy view with.
     */
    @NonNull
    @Override
    public Map<String, Object> modelOf(@Nullable Object data) {
        if (data == null) {
            return Collections.emptyMap();
        } else if (data instanceof SoyContextMediator) {
            return Collections.singletonMap(SOY_CONTEXT_SENTINEL, data);
        } else if (data instanceof Map) {
            //noinspection unchecked
            return (Map<String, Object>) data;
        } else {
            return BeanMap.of(data);
        }
    }

    /**
     * @param viewName view name to be rendered
     * @param data     response body to render it with a view
     * @param request  HTTP request
     * @param response HTTP response object assembled so far.
     * @return A writable where the view will be written to.
     */
    @NonNull
    @Override
    public Flowable<MutableHttpResponse<?>> render(@NonNull String viewName,
                                                   @Nullable Object data,
                                                   @NonNull HttpRequest<?> request,
                                                   @NonNull MutableHttpResponse<Object> response) {
        ArgumentUtils.requireNonNull("viewName", viewName);
        LOG.debug("Preparing render for template path '{}'",  viewName);

        int statusCode = response.getStatus().getCode();
        if ((!(data instanceof Map) && !(data instanceof SoyData) && !(data instanceof SoyContext))
                || statusCode != 200) {
            // we were passed something other than context data for a render operation. so, duck out gracefully.
            LOG.debug("Data was not a `Map` or `SoyData`. Returning untouched by Soy for view '{}'.", viewName);
            return Flux.just(response);
        }

        Map<String, Object> injectedPropsOverlay = new HashMap<>(1);
        if (injectNonce) {
            Optional<Object> nonceObj = request.getAttribute(CspFilter.NONCE_PROPERTY);
            if (nonceObj.isPresent()) {
                String nonceValue = ((String) nonceObj.get());
                injectedPropsOverlay.put(INJECTED_NONCE_PROPERTY, nonceValue);
            }
        }

        final SoyContextMediator context;
        if (data instanceof SoyContextMediator) {
            context = (SoyContextMediator) data;
        } else if (data instanceof Map) {
            Map dataMap = (Map) data;
            if (dataMap.size() == 1 && dataMap.containsKey(SOY_CONTEXT_SENTINEL)) {
                // it's a packaged soy context
                context = (SoyContextMediator)dataMap.get(SOY_CONTEXT_SENTINEL);
            } else {
                // otherwise, use it directly as a map
                //noinspection unchecked
                context = SoyContext.fromMap(
                        (Map<String, Object>) dataMap,
                        Optional.of(Collections.emptyMap()),
                        Optional.empty());
            }
        } else {
            context = SoyContext.fromMap(
                    modelOf(data),
                    Optional.of(Collections.emptyMap()),
                    Optional.empty());
        }

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
        renderer.setData(context.getProperties());
        renderer.setIj(context.getInjectedProperties(injectedPropsOverlay));

        if (this.soyMicronautConfiguration.isRenamingEnabled()) {
            SoyNamingMapProvider renamingProvider = (
                    context.overrideNamingMap().orElse(this.namingMapProvider));
            if (renamingProvider != null) {
                SoyCssRenamingMap cssMap = renamingProvider.cssRenamingMap();
                SoyIdRenamingMap idMap = renamingProvider.idRenamingMap();
                if (cssMap != null) {
                    renderer.setCssRenamingMap(cssMap);
                }
                if (idMap != null) {
                    renderer.setXidRenamingMap(idMap);
                }
            }
        }

        // handle a streaming message digester for etags, if directed
        final MessageDigest digester;
        if (context.enableETags() && context.strongETags()) {
            try {
                digester = MessageDigest.getInstance(ETAG_HASH_ALGORITHM);
                this.digesterActive = true;
            } catch (NoSuchAlgorithmException nsa) {
                throw new IllegalStateException(nsa);
            }
        } else {
            digester = null;
        }

        // prime the initial render
        return Flowable.<ByteBuffer, SoyRender>generate(SoyRender::create, (buffer, emitter) -> {
            // trigger initial render cycle, which may finish the response
            try {
                final SoySauce.WriteContinuation op = buffer.getContinuation();
                if (op == null) {
                    // no continuation means we are doing the first render run, which may complete it
                    LOG.trace("Initial render for view '" + viewName + "'");
                    handleRender(renderer.renderHtml(buffer), buffer, emitter, digester);
                } else {
                    // otherwise, pick up where we left off
                    LOG.trace("Continue render for view '" + viewName + "'");
                    continueRender(op, buffer, emitter, digester);
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
        }, SoyRender::close).map((buffer) -> context.finalizeResponse(request, response, buffer, digester));
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
