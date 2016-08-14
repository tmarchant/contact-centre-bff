package com.johnlewis.contactcentre.bff.product.verticle.client;

import com.johnlewis.contactcentre.bff.external.AtgApiProvider;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.ProxyOptions;

public class AtgProductApiClient extends ProductApiClient {

    private final String SEARCH_PRODUCTS_PATH = "/api/rest/v2/catalog/products/search";
    private final String PRODUCT_DETAILS_PATH = "/api/rest/v2/catalog/products/";

    private HttpClient httpClient;

    @Override
    public void start() throws Exception {
        HttpClientOptions options = AtgApiProvider.getHttpClientOptions(config());
        httpClient = vertx.createHttpClient(options);

        System.out.println("AtgProductApiClient:: started");
    }

    @Override
    public Future<RawJsonResponse> getSearchResults(String query, int pageSize) {
        Future<RawJsonResponse> responseFuture = Future.future();

        httpClient.get(SEARCH_PRODUCTS_PATH+"?q="+query+"&pageSize="+pageSize)
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body -> {
                    responseFuture.complete(new RawJsonResponse(response.statusCode(), body.toString()));
                }))
                .end();

        return responseFuture;
    }

    @Override
    public Future<RawJsonResponse> getProductById(String productId) {
        Future<RawJsonResponse> responseFuture = Future.future();

        httpClient.get(PRODUCT_DETAILS_PATH+productId)
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body -> {
                    responseFuture.complete(new RawJsonResponse(response.statusCode(), body.toString()));
                }))
                .end();

        return responseFuture;
    }
}
