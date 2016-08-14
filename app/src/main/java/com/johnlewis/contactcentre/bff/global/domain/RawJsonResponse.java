package com.johnlewis.contactcentre.bff.global.domain;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RawJsonResponse {
    private final int statusCode;
    private final String data;

    public RawJsonResponse(String data) {
        this.statusCode = HttpResponseStatus.OK.code();
        this.data = data;
    }

    public static RawJsonResponse from(JsonObject jsonObject) {
        return new RawJsonResponse(Json.encode(jsonObject));
    }
}
