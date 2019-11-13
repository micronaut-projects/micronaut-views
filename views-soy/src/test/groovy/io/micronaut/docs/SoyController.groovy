package io.micronaut.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future


@Requires(property = "spec.name", value = "soy")
@Controller("/soy")
class SoyController {
    private static final Logger LOG = LoggerFactory.getLogger(SoyController.class)
    private ExecutorService executor = Executors.newSingleThreadExecutor()

    @View("sample.home")
    @Get("/")
    HttpResponse index() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    @View("sample.home")
    @Get("/invalidContext")
    HttpResponse invalidContext() {
        return HttpResponse.ok(CollectionUtils.mapOf("username", "sgammon", "loggedIn", "yo"))
    }

    @View("i.do.not.exist")
    @Get("/missing")
    HttpResponse missingTemplate() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    @Get("/home")
    HttpResponse home() {
        return HttpResponse.ok(CollectionUtils.mapOf("loggedIn", true, "username", "sgammon"))
    }

    private Future<String> asyncTask() {
        LOG.info("Kicking off async task")
        return executor.submit({
            Thread.sleep(5000)
            LOG.info("Emitting async content")
            executor.shutdown()
            LOG.info("Told executor to kick rocks")
            return "Hello async content!"
        }) as Future<String>
    }

    @View("sample.home")
    @Get("/asyncContent")
    HttpResponse asyncContent() {
        LOG.info("Rendering async Soy page.")
        Future<String> task = asyncTask()
        assert task, "Task should not be null"
        LOG.info("Task is immediately: " + (task.isDone() ? "DONE." : "NOT DONE."))
        return HttpResponse.ok(CollectionUtils.mapOf(
                "loggedIn", true,
                "username", "sgammon",
                "message", task))
    }
}
