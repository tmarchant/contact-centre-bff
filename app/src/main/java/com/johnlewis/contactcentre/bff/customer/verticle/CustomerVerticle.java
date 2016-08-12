package com.johnlewis.contactcentre.bff.customer.verticle;

import com.johnlewis.contactcentre.bff.RoutableVerticle;
import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CustomerVerticle extends RoutableVerticle {

    public CustomerVerticle(Router router) {
        super(router);
    }

    @Override
    protected void routeMe(Router router) {
        router.get("/v1/customers").handler(this::searchCustomers);
    }

    private void searchCustomers(RoutingContext routingContext) {
        String query = routingContext.request().params().get("q");

        System.out.println("Searching customers with query: " + query);

        CustomerSearchResults searchResults = getMockCustomerResults();

        routingContext.response()
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(searchResults));
    }

    @NotNull
    private CustomerSearchResults getMockCustomerResults() {
        return new CustomerSearchResults
                    (Arrays.asList(new Customer("1", "Tom")));
    }

    @Override
    public void start() throws Exception {
        System.out.println("CustomerVerticle:: started");
    }
}
