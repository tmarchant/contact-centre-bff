package com.johnlewis.contactcentre.bff.ordercapture.domain;

import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class CreateOrderCaptureResponse {
    private final String orderCaptureId;
    private final String sessionId;
}
