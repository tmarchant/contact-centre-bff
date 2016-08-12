package com.johnlewis.contactcentre.bff.global.domain;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class RawJsonResponse {
    private final String data;

    public static RawJsonResponse from(JsonObject jsonObject) {
        return new RawJsonResponse(Json.encode(jsonObject));
    }
}
