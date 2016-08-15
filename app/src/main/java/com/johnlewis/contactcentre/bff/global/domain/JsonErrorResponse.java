package com.johnlewis.contactcentre.bff.global.domain;

import io.vertx.core.json.JsonObject;
import lombok.Getter;

public class JsonErrorResponse extends JsonResponse {

    @Getter private final String code;
    @Getter private final String message;

    public JsonErrorResponse(int statusCode, String code, String message) {
        super(statusCode, new JsonObject());

        this.code = code;
        this.message = message;

        data.put("code", code);
        data.put("message", message);
    }
}
