package com.johnlewis.contactcentre.bff

import spock.lang.Specification

class JsonConfigFileLoaderSpec extends Specification {

    def "loads json from file"() {
        given:
        JsonConfigFileLoader loader = new JsonConfigFileLoader();
        def json = loader.load('data/test-config.json');

        expect:
        json.size() == 3
        json.getString('config.string.var') == 'string value'
        json.getInteger('config.number.var') == 123
        json.getBoolean('config.boolean.var') == true
    }

    def "loads json from multiple files, overriding duplicate properties"() {
        given:
        JsonConfigFileLoader loader = new JsonConfigFileLoader();
        def json = loader.loadAllPresent('data/test-config.json', 'data/test-config-overrides.json');

        expect:
        json.size() == 4
        json.getString('config.string.var') == 'overridden value'
        json.getInteger('config.number.var') == 123
        json.getBoolean('config.boolean.var') == false
        json.getString('new.var') == 'new value'
    }

    def "loads json from multiple files, skipping files that don't exist"() {
        given:
        JsonConfigFileLoader loader = new JsonConfigFileLoader();
        def json = loader.loadAllPresent('data/test-config.json', 'data/i-dont-exit.json');

        expect:
        json.size() == 3
        json.getString('config.string.var') == 'string value'
    }
}
