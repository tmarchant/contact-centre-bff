package com.johnlewis.contactcentre.bff.global.domain;

import io.vertx.core.json.Json;
import lombok.Data;

@Data
public class ErrorResponse {
    final String code;
    final String message;

    public String toJson() {
        return Json.encode(this);
    }
}
