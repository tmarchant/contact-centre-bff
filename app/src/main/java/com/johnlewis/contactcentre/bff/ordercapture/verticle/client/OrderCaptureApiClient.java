package com.johnlewis.contactcentre.bff.ordercapture.verticle.client;

import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.domain.CreateOrderCaptureResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class OrderCaptureApiClient extends AbstractVerticle {
    public abstract Future<RawJsonResponse> create();
    public abstract Future<RawJsonResponse> get(String orderCaptureId, String token);
    public abstract Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token);
}
