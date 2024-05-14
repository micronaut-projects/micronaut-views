package io.micronaut.views

import io.micronaut.http.MediaType
import spock.lang.Specification

class MediaTypeParseSpec extends Specification {

    void "string based media type extraction works as expected for '#type'"() {
        expect:
        DefaultModelAndViewRenderer.mediaTypeName(type) == expected

        where:
        type                                     || expected
        null                                     || null
        ''                                       || ''
        'application/json'                       || MediaType.APPLICATION_JSON
        'application/json;charset=utf-8'         || MediaType.APPLICATION_JSON
        'application/json; charset=utf-8'        || MediaType.APPLICATION_JSON
        'application/json ; charset=utf-8'       || MediaType.APPLICATION_JSON
        'application/json ;charset=utf-8'        || MediaType.APPLICATION_JSON
        'application/json; charset=utf-8; q=0.8' || MediaType.APPLICATION_JSON
    }
}
