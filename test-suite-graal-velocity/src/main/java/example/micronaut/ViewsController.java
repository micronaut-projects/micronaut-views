package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Controller("/views")
public class ViewsController {

    @Get("/velocity")
    public ModelAndView<User> velocity() {
        return new ModelAndView<>("velocity/home", new User("test-user", true));
    }

    @View("/velocity/home")
    @Get("/velocity-view")
    public HttpResponse<User> velocityPojoView() {
        return HttpResponse.ok(new User("test-user", true));
    }
}
