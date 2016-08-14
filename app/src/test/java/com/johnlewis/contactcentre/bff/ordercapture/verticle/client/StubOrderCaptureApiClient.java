package com.johnlewis.contactcentre.bff.ordercapture.verticle.client;

import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.domain.CreateOrderCaptureResponse;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class StubOrderCaptureApiClient extends OrderCaptureApiClient {

    @Setter
    private RawJsonResponse createOrderCaptureResponse;
    @Setter
    private RawJsonResponse addItemResponse;
    @Setter
    private RawJsonResponse orderCaptureResponse;
    @Getter
    private String lastRequestToken;
    @Getter
    private AddItemRequest lastAddItemRequest;
    @Getter
    private String lastRequestOrderCaptureId;

    public StubOrderCaptureApiClient() {
        System.out.println("StubOrderCaptureApiClient:: started");
    }

    @Override
    public Future<RawJsonResponse> create() {
        Future<RawJsonResponse> future = Future.future();
        future.complete(createOrderCaptureResponse);

        return future;
    }

    @Override
    public Future<RawJsonResponse> get(String orderCaptureId, String token) {
        this.lastRequestToken = token;
        this.lastRequestOrderCaptureId = orderCaptureId;

        Future<RawJsonResponse> future = Future.future();
        future.complete(orderCaptureResponse);

        return future;
    }

    @Override
    public Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token) {
        this.lastRequestToken = token;
        this.lastRequestOrderCaptureId = orderCaptureId;
        this.lastAddItemRequest = new AddItemRequest(skuId, quantity);

        Future<RawJsonResponse> future = Future.future();
        future.complete(addItemResponse);

        return future;
    }

    @Data
    public static class AddItemRequest {
        private final String skuId;
        private final int quantity;
    }
}
