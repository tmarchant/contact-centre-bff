package com.johnlewis.contactcentre.bff.ordercapture.verticle.repository;

import com.johnlewis.contactcentre.bff.global.domain.JsonResponse;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.StubOrderCaptureApiClient;
import io.vertx.core.Future;
import lombok.Getter;
import lombok.Setter;

public class StubOrderCaptureRepository extends OrderCaptureRepository {

    @Setter
    private RawJsonResponse createOrderCaptureResponse;
    @Setter
    private RawJsonResponse addItemResponse;
    @Setter
    private JsonResponse orderCaptureResponse;
    @Getter
    private String lastRequestToken;
    @Getter
    private StubOrderCaptureApiClient.AddItemRequest lastAddItemRequest;
    @Getter
    private String lastRequestOrderCaptureId;
    @Setter
    private int setCustomerResponseCode = 200;

    public StubOrderCaptureRepository() {
        System.out.println("StubOrderCaptureRepository:: started");
    }

    @Override
    public Future<RawJsonResponse> create() {
        Future<RawJsonResponse> future = Future.future();
        future.complete(createOrderCaptureResponse);

        return future;
    }

    @Override
    public Future<JsonResponse> get(String orderCaptureId, String token) {
        this.lastRequestToken = token;
        this.lastRequestOrderCaptureId = orderCaptureId;

        Future<JsonResponse> future = Future.future();
        future.complete(orderCaptureResponse);

        return future;
    }

    @Override
    public Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token) {
        this.lastRequestToken = token;
        this.lastRequestOrderCaptureId = orderCaptureId;
        this.lastAddItemRequest = new StubOrderCaptureApiClient.AddItemRequest(skuId, quantity);

        Future<RawJsonResponse> future = Future.future();
        future.complete(addItemResponse);

        return future;
    }

    @Override
    public Future<RawJsonResponse> setCustomer(String orderCaptureId, String customerId, String token) {
        this.lastRequestToken = token;
        this.lastRequestOrderCaptureId = orderCaptureId;

        Future<RawJsonResponse> future = Future.future();
        future.complete(new RawJsonResponse(setCustomerResponseCode, ""));

        return future;
    }
}
