from micronaut.context.annotation import Requires
from micronaut.http import HttpResponse
from micronaut.http.annotation import Controller, Get
from micronaut.views import ModelAndView, View

from .Person import Person


@Requires(property="spec.name", value="ViewsControllerTest")
# tag::clazz[]
@Controller("/views")
class ViewsController:
# end::clazz[]

    # tag::map[]
    @View("home")  # <1>
    @Get
    def index(self) -> HttpResponse:
        return HttpResponse.ok({"loggedIn": True, "username": "sdelamo"})
    # end::map[]

    # tag::pojo[]
    @View("home")  # <1>
    @Get("/pojo")
    def pojo(self) -> HttpResponse:
        return HttpResponse.ok(Person("sdelamo", True))
    # end::pojo[]

    # tag::modelAndView[]
    @Get("/modelAndView")
    def model_and_view(self) -> ModelAndView:
        return ModelAndView("home", Person("sdelamo", True))
    # end::modelAndView[]
# tag::endclass[]
# end::endclass[]
