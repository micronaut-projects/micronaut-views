package io.micronaut.views.jstachio;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpRequest;
import io.micronaut.views.ViewsRenderer;
import jakarta.inject.Singleton;

@Requires(property = "spec.name", value = "HtmlControllerSpec")
@Singleton
public class HtmlViewsRenderer implements ViewsRenderer<Person, HttpRequest<?>> {
    @Override
    public Writable render(String viewName, Person person, HttpRequest<?> request) {
        return out -> out.write("<!DOCTYPE html><html><head></head><body><h1>" + person.firstName() + "</h1></body></html>");
    }

    @Override
    public boolean exists(String viewName) {
        return viewName.equals("foo");
    }
}
