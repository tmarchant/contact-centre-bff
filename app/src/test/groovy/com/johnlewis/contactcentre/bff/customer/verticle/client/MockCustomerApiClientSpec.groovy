package com.johnlewis.contactcentre.bff.customer.verticle.client

import spock.lang.Specification

class MockCustomerApiClientSpec extends Specification {

    def "decode JSON test file into Customer instances"() {
        given:
        def client = new MockCustomerApiClient("data/test-customers.json")

        expect:
        client.customers.size() == 2

        client.customers[0].id == '3'
        client.customers[0].name == 'Mr Krombopulus Michael'
    }
}
