package com.johnlewis.contactcentre.bff.global.domain;

import io.vertx.core.json.Json;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    final String code;
    final String message;

    public String toJson() {
        return Json.encode(this);
    }
}
