from micronaut.context.annotation import Requires
from micronaut.http import HttpResponse
from micronaut.http.annotation import Controller, Get
from micronaut.views import View


@Requires(property="spec.name", value="soy")
# tag::clazz[]
@Controller("/soy")
class SoyController:

    @View("sample.home")
    @Get
    def home(self) -> HttpResponse:
        return HttpResponse.ok({"loggedIn": True, "username": "sgammon"})
# end::clazz[]
