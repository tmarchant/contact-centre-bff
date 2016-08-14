package com.johnlewis.contactcentre.bff.customer.verticle;

import com.johnlewis.contactcentre.bff.RoutableVerticle;
import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import com.johnlewis.contactcentre.bff.customer.verticle.client.CustomerApiClient;
import com.johnlewis.contactcentre.bff.customer.verticle.client.MockCustomerApiClient;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CustomerVerticle extends RoutableVerticle {

    private final CustomerApiClient customerApiClient;

    public CustomerVerticle(Router router) {
        this(router, new MockCustomerApiClient());
    }

    public CustomerVerticle(Router router, CustomerApiClient customerApiClient) {
        super(router);
        this.customerApiClient = customerApiClient;
    }

    @Override
    protected void routeMe(Router router) {
        router.get("/v1/customers").handler(this::searchCustomers);
    }

    private void searchCustomers(RoutingContext routingContext) {
        String query = routingContext.request().params().get("q");

        System.out.println("Searching customers with query: " + query);

        customerApiClient.searchCustomers(query).setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(f.result()));
        });
    }

    @Override
    public void start() throws Exception {
        System.out.println("CustomerVerticle:: started");
    }
}
