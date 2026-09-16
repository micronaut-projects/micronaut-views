from micronaut.context.annotation import Requires
from micronaut.http.annotation import Controller, Get
from micronaut.security.annotation import Secured
from micronaut.security.rules import SecurityRule
from micronaut.views import View


@Requires(property="spec.name", value="SecurityViewModelProcessorSpec")
# tag::class[]
@Controller("/")
class BooksController:

    @Secured(SecurityRule.IS_AUTHENTICATED)
    @View("securitydecorator")
    @Get
    def index(self) -> dict[str, object]:
        return {"books": ["Developing Microservices"]}
# end::class[]
