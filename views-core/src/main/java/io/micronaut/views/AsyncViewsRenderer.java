package io.micronaut.views;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import org.reactivestreams.Publisher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Asynchronous rendering interface for views in Micronaut. This interface works with reactive types to allow the event
 * loop to take over when the renderer is paused, in cases where renderers support such signals.
 *
 * @see ViewsRenderer for the synchronous version
 * @author Sam Gammon
 * @since 1.3.0
 */
public interface AsyncViewsRenderer extends BaseViewsRenderer {
  /**
   * @param viewName view name to be render
   * @param data     response body to render it with a view
   * @return A writable where the view will be written to.
   */
  @Nonnull
  Publisher<MutableHttpResponse<?>> render(@Nonnull String viewName, @Nullable Object data);

  /**
   * @param viewName view name to be render
   * @param data     response body to render it with a view
   * @param request  HTTP request
   * @return A writable where the view will be written to.
   */
  default @Nonnull Publisher<MutableHttpResponse<?>> render(
    @Nonnull String viewName, @Nullable Object data, @Nonnull HttpRequest<?> request) {
    return render(viewName, data);
  }

  /**
   * @param viewName view name to be render
   * @param data     response body to render it with a view
   * @param request  HTTP request
   * @param response HTTP response object.
   * @return A writable where the view will be written to.
   */
  default @Nonnull Publisher<MutableHttpResponse<?>> render(
    @Nonnull String viewName,
    @Nullable Object data,
    @Nonnull HttpRequest<?> request,
    @Nonnull HttpResponse<?> response) {
    return render(viewName, data);
  }
}
