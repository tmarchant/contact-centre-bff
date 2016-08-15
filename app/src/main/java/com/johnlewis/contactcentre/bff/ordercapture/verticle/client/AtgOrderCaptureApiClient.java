package com.johnlewis.contactcentre.bff.ordercapture.verticle.client;

import com.johnlewis.contactcentre.bff.external.AtgApiProvider;
import com.johnlewis.contactcentre.bff.global.domain.Cookies;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.domain.CreateOrderCaptureResponse;
import io.netty.handler.codec.http.HttpStatusClass;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

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
    public Future<RawJsonResponse> create() {
        Future<RawJsonResponse> responseFuture = Future.future();

        httpClient.post(BASKET_PATH)
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body -> {
                    final HttpStatusClass status = HttpStatusClass.valueOf(response.statusCode());

                    if (!status.equals(HttpStatusClass.SUCCESS)) {
                        responseFuture.complete(new RawJsonResponse(response.statusCode(), body.toString()));
                    } else {
                        handleCreateAgentBasketResponse(response, body, responseFuture);
                    }
                }))
                .end();

        return responseFuture;
    }

    @Override
    public Future<RawJsonResponse> get(String orderCaptureId, String token) {
        Future<RawJsonResponse> responseFuture = Future.future();

        httpClient.get(buildGetOrderCaptureUrl(orderCaptureId, token))
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body -> {
                    responseFuture.complete(new RawJsonResponse(response.statusCode(), body.toString()));
                }))
                .end();

        return responseFuture;
    }

    @Override
    public Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token) {
        Future<RawJsonResponse> responseFuture = Future.future();

        httpClient.post(buildAddItemRequestUrl(orderCaptureId, token))
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body ->
                        responseFuture.complete(new RawJsonResponse(response.statusCode(), body.toString()))))
                .end();

        return responseFuture;
    }

    @Override
    public Future<RawJsonResponse> setCustomer(String orderCaptureId, String customerId, String token) {
        Future<RawJsonResponse> responseFuture = Future.future();

        JsonObject setCustomerJson = new JsonObject();
        setCustomerJson.put("id", customerId);

        httpClient.post(buildSetCustomerRequestUrl(orderCaptureId, token))
                .putHeader("Content-Type", "application/json")
                .putHeader("User-Agent", "Vert.x")
                .handler(response -> response.bodyHandler(body ->
                        responseFuture.complete(new RawJsonResponse(response.statusCode(), body.toString()))))
                .end(setCustomerJson.encode());

        return responseFuture;
    }

    private String buildSetCustomerRequestUrl(String orderCaptureId, String token) {
        return BASKET_PATH+"/"+orderCaptureId+"/customer?token="+token;
    }

    private String buildAddItemRequestUrl(String orderCaptureId, String token) {
        return BASKET_PATH+"/"+orderCaptureId+"/items?token="+token;
    }

    private String buildGetOrderCaptureUrl(String orderCaptureId, String token) {
        return BASKET_PATH+"/"+orderCaptureId+"?token="+token;
    }

    private void handleCreateAgentBasketResponse
            (HttpClientResponse response, Buffer body, Future<RawJsonResponse> responseFuture) {

        String sessionId = Cookies.extractCookieValue(response.cookies(), "JSESSIONID");
        String basketId = body.toJsonObject().getString("basketId");

        CreateOrderCaptureResponse createOrderCaptureResponse =
            new CreateOrderCaptureResponse(basketId, sessionId);

        responseFuture.complete(new RawJsonResponse
                (response.statusCode(), Json.encode(createOrderCaptureResponse)));
    }
}
