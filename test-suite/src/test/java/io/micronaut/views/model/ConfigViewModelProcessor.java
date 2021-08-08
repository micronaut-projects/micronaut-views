package io.micronaut.views.model;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.views.ModelAndView;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Map;

@Requires(property = "spec.name", value = "ModelAndViewSpec")
//tag::class[]
@Singleton
public class ConfigViewModelProcessor implements ViewModelProcessor<Map<String, Object>> {
    private final ApplicationConfiguration config;

    @Inject
    ConfigViewModelProcessor(ApplicationConfiguration environment) {
        this.config = environment;
    }


    @Override
    public void process(@NonNull HttpRequest<?> request, @NonNull ModelAndView<Map<String, Object>> modelAndView) {
        modelAndView.getModel().ifPresent(model -> model.put("config", config));
    }
}
//end::class[]