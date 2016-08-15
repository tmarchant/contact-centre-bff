package com.johnlewis.contactcentre.bff.ordercapture.verticle;

import com.johnlewis.contactcentre.bff.RoutableVerticle;
import com.johnlewis.contactcentre.bff.global.domain.JsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.repository.OrderCaptureRepository;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class OrderCaptureVerticle extends RoutableVerticle {

    private final OrderCaptureRepository orderCaptureRepository;

    public OrderCaptureVerticle(Router router,
                                OrderCaptureRepository orderCaptureRepository) {
        super(router);
        this.orderCaptureRepository = orderCaptureRepository;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("OrderCaptureVerticle:: started");
        startFuture.complete();
    }

    @Override
    protected void routeMe(Router router) {
        router.post("/v1/order-capture").handler(this::create);
        router.get("/v1/order-capture/:orderCaptureId").handler(this::get);
        router.post("/v1/order-capture/:orderCaptureId/items").handler(this::addItem);
        router.post("/v1/order-capture/:orderCaptureId/customer").handler(this::setCustomer);
    }

    private void create(RoutingContext routingContext) {
        orderCaptureRepository.create().setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(f.result().getStatusCode())
                    .end(f.result().getData());
        });
    }

    private void get(RoutingContext routingContext) {
        String orderCaptureId = routingContext.request().params().get("orderCaptureId");
        String token = routingContext.request().params().get("token");

        orderCaptureRepository.get(orderCaptureId, token).setHandler(f -> {
            JsonResponse orderCaptureResponse = f.result();

            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(orderCaptureResponse.getStatusCode())
                    .end(orderCaptureResponse.getData().encode());
        });
    }

    private void addItem(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {
            JsonObject bodyAsJson = body.toJsonObject();

            String orderCaptureId = routingContext.request().params().get("orderCaptureId");
            String token = routingContext.request().params().get("token");

            String skuId = bodyAsJson.getString("skuId");
            int quantity = bodyAsJson.getInteger("quantity");

            orderCaptureRepository.addItem(orderCaptureId, skuId, quantity, token).setHandler(f -> {
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .end(f.result().getData());
            });
        });
    }

    private void setCustomer(RoutingContext routingContext) {
        routingContext.request().bodyHandler(body -> {
            JsonObject bodyAsJson = body.toJsonObject();

            String orderCaptureId = routingContext.request().params().get("orderCaptureId");
            String token = routingContext.request().params().get("token");

            String customerId = bodyAsJson.getString("id");

            orderCaptureRepository.setCustomer(orderCaptureId, customerId, token).setHandler(f -> {
                routingContext.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(f.result().getStatusCode())
                        .end(f.result().getData());
            });
        });
    }
}
