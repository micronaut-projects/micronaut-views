package io.micronaut.views.model;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.views.ModelAndView;

import javax.inject.Singleton;
import java.util.Map;

@Requires(property = "spec.name", value = "ModelAndViewSpec")
//tag::class[]
@Singleton // <1>
public class ConfigViewModelProcessor implements ViewModelProcessor<Map<String, Object>, HttpRequest<?>> {
    private final ApplicationConfiguration config;

    ConfigViewModelProcessor(ApplicationConfiguration environment) {
        this.config = environment;
    }

    @Override
    public void process(@NonNull HttpRequest<?> request,
                        @NonNull ModelAndView<Map<String, Object>> modelAndView) {
        modelAndView.getModel()
                .ifPresent(model -> {
                    if (config.getName().isPresent()) {
                        model.put("applicationName", config.getName().get());
                    }
                });
    }
}
//end::class[]
