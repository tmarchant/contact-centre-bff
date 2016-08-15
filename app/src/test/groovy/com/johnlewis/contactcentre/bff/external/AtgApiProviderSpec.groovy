package com.johnlewis.contactcentre.bff.external

import io.vertx.core.json.JsonObject
import spock.lang.Specification

class AtgApiProviderSpec extends Specification {

    def "loads config without proxy settings"() {
        given:
        JsonObject config = new JsonObject()

        config.put("atg.host", "default.host")
        config.put("atg.port", 8080)

        when:
        def httpClient = AtgApiProvider.getHttpClientOptions(config)

        then:
        httpClient.getDefaultHost() == "default.host"
        httpClient.getDefaultPort() == 8080
        httpClient.getProxyOptions() == null
        httpClient.ssl == false
    }

    def "loads ssl config without proxy settings"() {
        given:
        JsonObject config = new JsonObject()

        config.put("atg.host", "default.host")
        config.put("atg.port", 443)

        when:
        def httpClient = AtgApiProvider.getHttpClientOptions(config)

        then:
        httpClient.getDefaultHost() == "default.host"
        httpClient.getDefaultPort() == 443
        httpClient.ssl == true
    }

    def "loads ssl config with proxy settings"() {
        given:
        JsonObject config = new JsonObject()

        config.put("atg.host", "default.host")
        config.put("atg.port", 443)

        config.put("atg.proxy.host", "proxy.host")
        config.put("atg.proxy.port", 1234)

        when:
        def httpClient = AtgApiProvider.getHttpClientOptions(config)

        then:
        httpClient.getProxyOptions().host == "proxy.host"
        httpClient.getProxyOptions().port == 1234
    }
}
