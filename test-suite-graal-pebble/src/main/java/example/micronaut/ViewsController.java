package example.micronaut;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;

@Controller("/views")
public class ViewsController {

    @Get("/pebble")
    public ModelAndView<User> pebble() {
        return new ModelAndView<>("pebble/home", new User("test-user", true));
    }

    @View("/pebble/home")
    @Get("/pebble-view")
    public HttpResponse<User> pebblePojoView() {
        return HttpResponse.ok(new User("test-user", true));
    }
}
