package com.johnlewis.contactcentre.bff.ordercapture.verticle.repository;

import com.johnlewis.contactcentre.bff.global.domain.JsonResponse;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class OrderCaptureRepository extends AbstractVerticle {
    public abstract Future<RawJsonResponse> create();
    public abstract Future<JsonResponse> get(String orderCaptureId, String token);
    public abstract Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token);
    public abstract Future<RawJsonResponse> setCustomer(String orderCaptureId, String customerId, String token);
}
