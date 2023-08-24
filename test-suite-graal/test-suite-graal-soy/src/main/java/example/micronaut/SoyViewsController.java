package example.micronaut;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Replaces(ViewsController.class)
@Controller("/views")
public class SoyViewsController {

    @Get("/model")
    public ModelAndView<User> modelView() {
        return new ModelAndView<>("sample.home", new User("test-user", true));
    }

    @View("sample.home")
    @Get("/pojo")
    public HttpResponse<User> pojoView() {
        return HttpResponse.ok(new User("test-user", true));
    }
}
