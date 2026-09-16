import java
from jakarta.inject import Singleton
from micronaut.context.annotation import Requires
from micronaut.runtime import ApplicationConfiguration
from micronaut.views import ModelAndView

# TODO(python): java.type needed because the Python snippet package `micronaut/views/model` shadows the generated
# `micronaut.views.model` module, so `from micronaut.views.model import ViewModelProcessor` resolves to the Python
# package (compiler fix merged, not yet released).
ViewModelProcessor = java.type("io.micronaut.views.model.ViewModelProcessor")


@Requires(property="spec.name", value="ModelAndViewSpec")
# tag::class[]
@Singleton  # <1>
class ConfigViewModelProcessor(ViewModelProcessor[dict, object]):

    def __init__(self, config: ApplicationConfiguration):
        self.config = config

    def process(self, request: object, modelAndView: ModelAndView) -> None:
        model = modelAndView.getModel()
        if model.isPresent() and self.config.getName().isPresent():
            model.get()["applicationName"] = self.config.getName().get()
# end::class[]
