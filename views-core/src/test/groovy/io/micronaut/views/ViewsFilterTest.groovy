package io.micronaut.views

import io.micronaut.context.ApplicationContext
import io.micronaut.context.DefaultApplicationContext
import io.micronaut.context.env.PropertySource
import io.micronaut.http.HttpRequest
import io.micronaut.views.model.Book
import io.micronaut.views.model.BookViewModelProcessor
import io.micronaut.views.model.LibraryViewModelProcessor
import spock.lang.Specification

import java.time.Instant

class ViewsFilterTest extends Specification {
    void "should locate view processors by generic type"() {
        given:
        ApplicationContext applicationContext = new DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(PropertySource.of("test",
                ['micronaut.views.enabled': true]
        ))
        applicationContext.start()
        ViewsFilter viewsFilter = new ViewsFilter(applicationContext)
        HttpRequest req = Mock(HttpRequest.class)
        Book model = new Book()
        ModelAndView<Book> modelAndView = new ModelAndView("foo", model)
        BookViewModelProcessor.HIT_COUNTER = 0
        LibraryViewModelProcessor.HIT_COUNTER = 0

        when:

        viewsFilter.enhanceModel(req, modelAndView)

        then:
        assert BookViewModelProcessor.HIT_COUNTER == 1
        assert LibraryViewModelProcessor.HIT_COUNTER == 0


        cleanup:
        applicationContext.close()

    }

    void "should locate view processors that match a supertype"() {
        given:
        ApplicationContext applicationContext = new DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(PropertySource.of("test",
                ['micronaut.views.enabled': true]
        ))
        applicationContext.start()
        ViewsFilter viewsFilter = new ViewsFilter(applicationContext)
        HttpRequest req = Mock(HttpRequest.class)
        ModelAndView<Object> modelAndView = new ModelAndView("foo", new Object())
        BookViewModelProcessor.HIT_COUNTER = 0
        LibraryViewModelProcessor.HIT_COUNTER = 0

        when:
        viewsFilter.enhanceModel(req, modelAndView)

        then:
        assert BookViewModelProcessor.HIT_COUNTER == 1
        assert LibraryViewModelProcessor.HIT_COUNTER == 1


        cleanup:
        applicationContext.close()

    }

    void "should not locate any view processors if none matches generic type"() {
        given:
        ApplicationContext applicationContext = new DefaultApplicationContext("test")
        applicationContext.environment.addPropertySource(PropertySource.of("test",
                ['micronaut.views.enabled': true]
        ))
        applicationContext.start()
        ViewsFilter viewsFilter = new ViewsFilter(applicationContext)
        HttpRequest req = Mock(HttpRequest.class)
        ModelAndView<Instant> modelAndView = new ModelAndView("foo", Instant.now())
        BookViewModelProcessor.HIT_COUNTER = 0
        LibraryViewModelProcessor.HIT_COUNTER = 0

        when:
        viewsFilter.enhanceModel(req, modelAndView)

        then:
        assert BookViewModelProcessor.HIT_COUNTER == 0
        assert LibraryViewModelProcessor.HIT_COUNTER == 0


        cleanup:
        applicationContext.close()

    }

}
