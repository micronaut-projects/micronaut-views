package io.micronaut.views.model

import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpRequest
import io.micronaut.views.ModelAndView
import jakarta.inject.Singleton

@Singleton
class LibraryViewModelProcessor implements ViewModelProcessor<Library> {
    public static int HIT_COUNTER

    @Override
    void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Library> modelAndView) {
        HIT_COUNTER++
    }
}
