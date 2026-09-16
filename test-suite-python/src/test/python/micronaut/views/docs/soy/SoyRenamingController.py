from micronaut.context.annotation import Requires
from micronaut.http.annotation import Controller, Get
from micronaut.views import View


@Requires(property="spec.name", value="soy")
@Controller("/soy/renaming")
class SoyRenamingController:

    @View("sample.renaming")
    @Get
    def renaming(self) -> dict[str, object]:
        return {}
