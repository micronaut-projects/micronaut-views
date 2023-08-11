package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Controller("/views")
public class ViewsController {

    @Get("/thymeleaf")
    public ModelAndView<User> thymeleaf() {
        return new ModelAndView<>("thymeleaf/dir/home", new User("test-user", true));
    }

    @Get("/thymeleaf-view")
    @View("/thymeleaf/dir/home")
    public HttpResponse<User> thymeleafPojoView() {
        return HttpResponse.ok(new User("test-user", true));
    }

}
