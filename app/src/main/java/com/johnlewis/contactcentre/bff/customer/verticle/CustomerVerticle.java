package com.johnlewis.contactcentre.bff.customer.verticle;

import com.johnlewis.contactcentre.bff.RoutableVerticle;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerId;
import com.johnlewis.contactcentre.bff.customer.verticle.repository.CustomerRepository;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CustomerVerticle extends RoutableVerticle {

    private final CustomerRepository customerRepository;

    public CustomerVerticle(Router router, CustomerRepository customerRepository) {
        super(router);
        this.customerRepository = customerRepository;
    }

    @Override
    protected void routeMe(Router router) {
        router.get("/v1/customers").handler(this::searchCustomers);
    }

    private void searchCustomers(RoutingContext routingContext) {
        String query = routingContext.request().params().get("q");

        System.out.println("Searching customers with query: " + query);

        customerRepository.searchCustomers(query).setHandler(f -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(f.result()));
        });
    }

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        MessageConsumer<String> consumer = eventBus.consumer("customer.get.by.id");

        consumer.handler(message -> {
            String customerId = message.body();

            customerRepository.getById(customerId)
                    .setHandler(f -> message.reply(Json.encode(f.result())));
        });

        System.out.println("CustomerVerticle:: started");
    }
}
