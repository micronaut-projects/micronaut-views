package io.micronaut.views.model

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.runtime.ApplicationConfiguration
import io.micronaut.views.ModelAndView
import javax.inject.Singleton

@Requires(property = "spec.name", value = "ModelAndViewSpec")
//tag::class[]
@Singleton // <1>
class ConfigViewModelProcessor internal constructor(private val config: ApplicationConfiguration) :
    ViewModelProcessor<MutableMap<String, Any>> {

    override fun process(request: HttpRequest<*>, modelAndView: ModelAndView<MutableMap<String, Any>>) {
        modelAndView.model
            .ifPresent { model ->
                config.name.ifPresent { name -> model["applicationName"] = name }
            }
    }
}
//end::class[]
