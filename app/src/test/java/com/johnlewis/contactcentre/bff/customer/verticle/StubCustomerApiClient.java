package com.johnlewis.contactcentre.bff.customer.verticle;

import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import com.johnlewis.contactcentre.bff.customer.verticle.client.CustomerApiClient;
import io.vertx.core.Future;
import lombok.Setter;

public class StubCustomerApiClient implements CustomerApiClient {

    @Setter
    private CustomerSearchResults customerSearchResults;

    @Override
    public Future<CustomerSearchResults> searchCustomers(String query) {
        Future<CustomerSearchResults> future = Future.future();
        future.complete(customerSearchResults);

        return future;
    }
}
