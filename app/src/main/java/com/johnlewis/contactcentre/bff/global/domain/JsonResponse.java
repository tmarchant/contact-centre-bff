package com.johnlewis.contactcentre.bff.global.domain;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JsonResponse {
    public static final JsonResponse EMPTY = new JsonResponse(new JsonObject());

    final int statusCode;
    final JsonObject data;

    public JsonResponse(JsonObject data) {
        this.statusCode = HttpResponseStatus.OK.code();
        this.data = data;
    }
}
