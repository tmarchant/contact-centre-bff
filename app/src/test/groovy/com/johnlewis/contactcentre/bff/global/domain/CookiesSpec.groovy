package com.johnlewis.contactcentre.bff.global.domain

import spock.lang.Specification
import spock.lang.Unroll

class CookiesSpec extends Specification {

    @Unroll
    def "cookie value extracted from cookie: #cookieName"() {
        given:
        def cookies = [ 'Chocolate=yum yum; Expires=Wed, 09 Jun 2021 10:18:14 GMT',
                        'Almond=nom nom']

        expect:
        Cookies.extractCookieValue(cookies, cookieName) == expectedCookieValue

        where:
        cookieName  | expectedCookieValue
        'Chocolate' | 'yum yum'
        'Almond'    | 'nom nom'
    }
}
