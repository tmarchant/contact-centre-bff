package com.johnlewis.contactcentre.bff.ordercapture.verticle.client;

import com.johnlewis.contactcentre.bff.external.AtgApiProvider;
import com.johnlewis.contactcentre.bff.global.domain.CookieTools;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.domain.CreateOrderCaptureResponse;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;

public class AtgOrderCaptureApiClient extends OrderCaptureApiClient {

    private final String BASKET_PATH = "/api/rest/v1/agent/baskets";

    private HttpClient httpClient;

    @Override
    public void start() throws Exception {
        HttpClientOptions options = AtgApiProvider.getHttpClientOptions(config());
        httpClient = vertx.createHttpClient(options);

        System.out.println("AtgOrderCaptureApiClient:: started");
    }

    @Override
    public Future<CreateOrderCaptureResponse> create() {
        Future<CreateOrderCaptureResponse> responseFuture = Future.future();

        httpClient.post(BASKET_PATH)
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> handleCreateAgentBasketResponse(response, responseFuture))
                .end();

        return responseFuture;
    }

    @Override
    public Future<RawJsonResponse> get(String orderCaptureId, String token) {
        return null;
    }

    @Override
    public Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token) {
        Future<RawJsonResponse> responseFuture = Future.future();

        httpClient.post(buildAddItemRequestUrl(orderCaptureId))
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body ->
                        responseFuture.complete(new RawJsonResponse(body.toString()))))
                .end();

        return responseFuture;
    }

    String buildAddItemRequestUrl(String orderCaptureId) {
        return BASKET_PATH+orderCaptureId+"/items";
    }

    String buildGetOrderCaptureUrl(String orderCaptureId) {
        return BASKET_PATH+orderCaptureId;
    }

    private void handleCreateAgentBasketResponse
            (HttpClientResponse response, Future<CreateOrderCaptureResponse> responseFuture) {
        System.out.println("Response received: " + response.statusCode());

        String sessionId = CookieTools.extractCookieValue(response.cookies(), "JSESSIONID");

        response.bodyHandler(body -> {
            String basketId = body.toJsonObject().getString("basketId");

            responseFuture.complete(new CreateOrderCaptureResponse(basketId, sessionId));
        });
    }
}
