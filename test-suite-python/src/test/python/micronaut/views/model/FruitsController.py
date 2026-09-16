from micronaut.context.annotation import Requires
from micronaut.http.annotation import Controller, Get
from micronaut.security.annotation import Secured
from micronaut.security.rules import SecurityRule
from micronaut.views import ModelAndView, View

from .Fruit import Fruit


@Secured(SecurityRule.IS_ANONYMOUS)
@Requires(property="spec.name", value="ModelAndViewSpec")
@Controller
class FruitsController:

    # tag::pojo[]
    @View("fruits")
    @Get
    def index(self) -> Fruit:
        return Fruit("apple", "red")
    # end::pojo[]

    @View("fruits")
    @Get("/null")
    def null_model(self) -> object | None:
        return None

    @View("fruits")
    @Get("/map")
    def collection_model(self) -> dict[str, object]:
        return {"fruit": Fruit("orange", "orange")}

    @Get("/processor")
    def processor(self) -> ModelAndView:
        return ModelAndView("fruits-processor", {"fruit": Fruit("orange", "orange")})
