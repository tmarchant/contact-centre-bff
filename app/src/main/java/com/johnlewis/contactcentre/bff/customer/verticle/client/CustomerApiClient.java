package com.johnlewis.contactcentre.bff.customer.verticle.client;

import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import io.vertx.core.Future;

public interface CustomerApiClient {
    Future<CustomerSearchResults> searchCustomers(String query);
}
