package com.johnlewis.contactcentre.bff.product.verticle.client;

import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import io.vertx.core.Future;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class StubProductApiClient extends ProductApiClient {

    @Setter
    private RawJsonResponse productSearchResponse;
    @Setter
    private RawJsonResponse productDetailsResponse;
    @Getter
    private LastSearchRequest lastSearchRequest;
    @Getter
    private LastProductRequest lastProductRequest;

    @Override
    public Future<RawJsonResponse> getSearchResults(String query, int pageSize) {
        this.lastSearchRequest = new LastSearchRequest(query, pageSize);

        Future<RawJsonResponse> future = Future.future();
        future.complete(productSearchResponse);

        return future;
    }

    @Override
    public Future<RawJsonResponse> getProductById(String productId) {
        this.lastProductRequest = new LastProductRequest(productId);

        Future<RawJsonResponse> future = Future.future();
        future.complete(productDetailsResponse);

        return future;
    }

    @Data
    public static class LastSearchRequest {
        private final String query;
        private final int pageSize;
    }

    @Data
    public static class LastProductRequest {
        private final String productId;
    }
}
