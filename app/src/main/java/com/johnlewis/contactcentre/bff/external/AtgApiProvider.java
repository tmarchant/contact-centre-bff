package com.johnlewis.contactcentre.bff.external;

import com.google.common.base.Strings;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.ProxyOptions;

public class AtgApiProvider {

    public static HttpClientOptions getHttpClientOptions(JsonObject config) {
        String host = config.getString("atg.host");
        Integer port = config.getInteger("atg.port");

        String proxyHost = config.getString("atg.proxy.host");

        HttpClientOptions options = new HttpClientOptions()
                .setDefaultHost(host)
                .setDefaultPort(port)
                .setSsl(port == 443);

        if (!Strings.isNullOrEmpty(proxyHost)) {
            Integer proxyPort = config.getInteger("atg.proxy.port");
            options.setProxyOptions(new ProxyOptions()
                    .setHost(proxyHost)
                    .setPort(proxyPort));
        }

        return options;
    }
}
