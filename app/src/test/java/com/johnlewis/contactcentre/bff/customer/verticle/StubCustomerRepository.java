package com.johnlewis.contactcentre.bff.customer.verticle;

import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerId;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import com.johnlewis.contactcentre.bff.customer.verticle.repository.CustomerRepository;
import io.vertx.core.Future;
import lombok.Setter;

public class StubCustomerRepository extends CustomerRepository {

    @Setter
    private CustomerSearchResults customerSearchResults;

    @Setter
    private Customer getByIdResponse;

    @Override
    public Future<CustomerSearchResults> searchCustomers(String query) {
        Future<CustomerSearchResults> future = Future.future();
        future.complete(customerSearchResults);

        return future;
    }

    @Override
    public Future<Customer> getById(String id) {
        Future<Customer> future = Future.future();
        future.complete(getByIdResponse);

        return future;
    }
}
