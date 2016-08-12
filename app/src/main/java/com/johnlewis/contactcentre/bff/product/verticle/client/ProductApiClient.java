package com.johnlewis.contactcentre.bff.product.verticle.client;

import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class ProductApiClient extends AbstractVerticle {
    public abstract Future<RawJsonResponse> getSearchResults(String query, int pageSize);
    public abstract Future<RawJsonResponse> getProductById(String productId);
}
