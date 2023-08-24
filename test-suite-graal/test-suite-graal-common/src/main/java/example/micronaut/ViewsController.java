package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Controller("/views")
public class ViewsController {

    @Get("/model")
    public ModelAndView<User> modelView() {
        return new ModelAndView<>("template/home", new User("test-user", true));
    }

    @View("/template/home")
    @Get("/pojo")
    public HttpResponse<User> pojoView() {
        return HttpResponse.ok(new User("test-user", true));
    }
}
