package com.johnlewis.contactcentre.bff.ordercapture.verticle.repository;

import com.johnlewis.contactcentre.bff.customer.verticle.repository.CustomerRepository;
import com.johnlewis.contactcentre.bff.global.domain.JsonResponse;
import com.johnlewis.contactcentre.bff.global.domain.RawJsonResponse;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.AtgOrderCaptureApiClient;
import com.johnlewis.contactcentre.bff.ordercapture.verticle.client.OrderCaptureApiClient;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class DefaultOrderCaptureRepository extends OrderCaptureRepository {

    private final OrderCaptureApiClient orderCaptureApiClient;
    private final CustomerRepository customerRepository;

    /* Temporary mapping table to manage associations between OrderCaptures and Customers. */
    @Setter
    private Map<String, String> ordersToCustomers;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.deployVerticle(orderCaptureApiClient, new DeploymentOptions().setConfig(config()), result -> {
            System.out.println("DefaultOrderCaptureRepository:: started");
            startFuture.complete();
        });
    }

    public DefaultOrderCaptureRepository(CustomerRepository customerRepository) {
        this(new AtgOrderCaptureApiClient(), customerRepository);
    }

    public DefaultOrderCaptureRepository(OrderCaptureApiClient orderCaptureApiClient,
                                         CustomerRepository customerRepository) {
        this.orderCaptureApiClient = orderCaptureApiClient;
        this.customerRepository = customerRepository;

        this.ordersToCustomers = new HashMap<>();
    }

    @Override
    public Future<RawJsonResponse> create() {
        return orderCaptureApiClient.create();
    }

    @Override
    public Future<JsonResponse> get(String orderCaptureId, String token) {
        Future<JsonResponse> future = Future.future();

        orderCaptureApiClient.get(orderCaptureId, token).setHandler(f -> {
            JsonObject orderCaptureJson = new JsonObject(f.result().getData());

            augmentOrderCaptureWithCustomerData(orderCaptureJson).setHandler(augmentedJsonResponse -> {
                future.complete(
                        new JsonResponse(f.result().getStatusCode(), augmentedJsonResponse.result()));
            });
        });

        return future;
    }

    private Future<JsonObject> augmentOrderCaptureWithCustomerData(JsonObject orderCaptureJson) {
        String orderCaptureId = orderCaptureJson.getString("id");
        Future<JsonObject> response = Future.future();

        if (ordersToCustomers.containsKey(orderCaptureId)) {
            String customerId = ordersToCustomers.get(orderCaptureId);
            customerRepository.getById(customerId).setHandler(f -> {
                String customerName = f.result().getName();

                JsonObject customerJson = new JsonObject();

                customerJson.put("id", customerId);
                customerJson.put("name", customerName);
                orderCaptureJson.put("customer", customerJson);

                response.complete(orderCaptureJson);
            });
        } else {
            response.complete(orderCaptureJson);
        }

        return response;
    }

    @Override
    public Future<RawJsonResponse> addItem(String orderCaptureId, String skuId, int quantity, String token) {
        return orderCaptureApiClient.addItem(orderCaptureId, skuId, quantity, token);
    }

    @Override
    public Future<RawJsonResponse> setCustomer(String orderCaptureId, String customerId, String token) {
        return orderCaptureApiClient.setCustomer(orderCaptureId, customerId, token);
    }
}
