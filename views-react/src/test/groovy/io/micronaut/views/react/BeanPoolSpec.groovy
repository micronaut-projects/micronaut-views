package io.micronaut.views.react

import io.micronaut.views.react.util.BeanPool
import spock.lang.Specification

import java.io.IOException

class BeanPoolSpec extends Specification {

    void "clear ignores AutoCloseable failures"() {
        given:
        AutoCloseable bean = { throw new IOException("expected") } as AutoCloseable
        BeanPool<AutoCloseable> pool = new BeanPool({ bean })
        pool.checkIn(pool.checkOut())

        when:
        pool.clear()

        then:
        noExceptionThrown()
    }

    void "clear closes pooled AutoCloseable beans"() {
        given:
        AutoCloseable bean = Mock()
        BeanPool<AutoCloseable> pool = new BeanPool({ bean })
        def handle = pool.checkOut()
        pool.checkIn(handle)

        when:
        pool.clear()

        then:
        1 * bean.close()
    }

    void "clear closes checked out AutoCloseable beans when they return"() {
        given:
        AutoCloseable bean = Mock()
        BeanPool<AutoCloseable> pool = new BeanPool({ bean })
        def handle = pool.checkOut()

        when:
        pool.clear()
        pool.checkIn(handle)

        then:
        1 * bean.close()
    }
}
