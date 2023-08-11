package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Controller("/views")
public class ViewsController {

    @Get("/handlebars")
    public ModelAndView<User> handlebars() {
        return new ModelAndView<>("handlebars/home", new User("test-user", true));
    }

    @View("/handlebars/home")
    @Get("/handlebars-view")
    public HttpResponse<User> handlebarsPojoView() {
        return HttpResponse.ok(new User("test-user", true));
    }
}
