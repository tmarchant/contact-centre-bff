package com.johnlewis.contactcentre.bff.ordercapture.verticle;

import com.johnlewis.contactcentre.bff.RoutableVerticle;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.AtgOrderCaptureApiClient;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.OrderCaptureApiClient;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class OrderCaptureVerticle extends RoutableVerticle {

    private OrderCaptureApiClient orderCaptureApiClient;

    public OrderCaptureVerticle(Router router) {
        this(router, new AtgOrderCaptureApiClient());
    }

    public OrderCaptureVerticle(Router router, OrderCaptureApiClient orderCaptureApiClient) {
        super(router);
        this.orderCaptureApiClient = orderCaptureApiClient;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.deployVerticle(orderCaptureApiClient, result -> {
            System.out.println("OrderCaptureVerticle:: started");
            startFuture.complete();
        });
    }

    @Override
    protected void routeMe(Router router) {
        router.post("/v1/order-capture").handler(this::create);
        router.get("/v1/order-capture/:orderCaptureId").handler(this::get);
        router.post("/v1/order-capture/:orderCaptureId/items").handler(this::addItem);
    }

    private void create(RoutingContext routingContext) {
        orderCaptureApiClient.create().setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encodePrettily(f.result()));
        });
    }

    private void get(RoutingContext routingContext) {
        String orderCaptureId = routingContext.request().params().get("orderCaptureId");
        String token = routingContext.request().params().get("token");

        orderCaptureApiClient.get(orderCaptureId, token).setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(f.result().getData());
        });
    }

    private void addItem(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {
            JsonObject bodyAsJson = body.toJsonObject();

            String orderCaptureId = routingContext.request().params().get("orderCaptureId");
            String token = routingContext.request().params().get("token");

            String skuId = bodyAsJson.getString("skuId");
            int quantity = bodyAsJson.getInteger("quantity");

            orderCaptureApiClient.addItem(orderCaptureId, skuId, quantity, token).setHandler(f -> {
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(f.result().getData());
            });
        });
    }
}
