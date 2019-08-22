package io.micronaut.views.soy;


import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.ViewsConfiguration;
import io.micronaut.views.ViewsRenderer;
import io.micronaut.views.exceptions.ViewRenderingException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;


/**
 * Renders views with a Soy Tofu-based engine.
 *
 * @author Sam Gammon
 * @since 1.3.0
 */
@Produces(MediaType.TEXT_HTML)
@Requires(property = SoyViewsRendererConfigurationProperties.PREFIX + ".engine", notEquals = "sauce")
@Requires(property = SoyViewsRendererConfigurationProperties.PREFIX + ".enabled", notEquals = "false")
@Singleton
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class SoyTofuViewsRenderer implements ViewsRenderer {

  protected final ViewsConfiguration viewsConfiguration;
  protected final SoyViewsRendererConfigurationProperties soyMicronautConfiguration;
  protected final SoyTofu soyTofu;

  /**
   * @param viewsConfiguration Views configuration properties.
   * @param soyConfiguration   Soy configuration properties.
   */
  @Inject
  SoyTofuViewsRenderer(ViewsConfiguration viewsConfiguration,
                       SoyViewsRendererConfigurationProperties soyConfiguration) {
    this.viewsConfiguration = viewsConfiguration;
    this.soyMicronautConfiguration = soyConfiguration;
    this.soyTofu = soyConfiguration.getFileSet().compileToTofu();
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
    final SoyTofu.Renderer renderer = soyTofu.newRenderer(viewName);
    renderer.setData(context);

    try {
      final AppendableToWritable target = new AppendableToWritable();
      renderer.renderHtml(target);
      return target;

    } catch (SoyTofuException e) {
      throw new ViewRenderingException(
        "Error rendering Soy Tofu view [" + viewName + "]: " + e.getMessage(), e);
    }
  }

  /**
   * @param view view name to be rendered
   * @return true if a template can be found for the supplied view name.
   */
  @Override
  public boolean exists(@Nonnull String view) {
    return soyTofu.hasTemplate(view);
  }

}
