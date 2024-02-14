package io.micronaut.views.thymeleaf

import io.micronaut.core.io.Writable

final class WriteableUtils {
    private WriteableUtils() {

    }

    static String writableToString(Writable writable) {
         return new StringWriter().with {
            writable.writeTo(it)
            it.toString()
        }
    }
}
