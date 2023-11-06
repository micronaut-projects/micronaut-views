package io.micronaut.views.fields.thymeleaf;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;

public class TestUtils {

    public static HttpRequest<?> htmlGet(String path) {
        return HttpRequest.GET(path).accept(MediaType.TEXT_HTML);
    }

    public static HttpRequest<?> formPost(String path, String body) {
        return HttpRequest.POST(path, body).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_HTML);
    }
}
