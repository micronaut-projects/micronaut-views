package io.micronaut.views.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpRequest
import io.micronaut.views.ModelAndView
import jakarta.inject.Singleton

@Singleton
class BookViewModelProcessor implements ViewModelProcessor<Book> {
    public static int HIT_COUNTER

    @Override
    void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Book> modelAndView) {
        HIT_COUNTER++
    }
}
