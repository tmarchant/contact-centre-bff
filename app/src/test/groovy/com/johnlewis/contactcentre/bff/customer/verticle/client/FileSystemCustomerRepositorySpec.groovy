package com.johnlewis.contactcentre.bff.customer.verticle.client

import com.johnlewis.contactcentre.bff.customer.verticle.repository.FileSystemCustomerRepository
import spock.lang.Specification

class FileSystemCustomerRepositorySpec extends Specification {

    def "decode JSON test file into Customer instances"() {
        given:
        def client = new FileSystemCustomerRepository("data/test-customers.json")

        expect:
        client.customers.size() == 2

        client.customers[0].id == '3'
        client.customers[0].name == 'Mr Krombopulus Michael'
    }
}
