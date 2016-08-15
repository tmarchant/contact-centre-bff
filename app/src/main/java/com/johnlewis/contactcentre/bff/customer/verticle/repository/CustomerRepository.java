package com.johnlewis.contactcentre.bff.customer.verticle.repository;

import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerId;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public abstract class CustomerRepository extends AbstractVerticle {
    public abstract Future<CustomerSearchResults> searchCustomers(String query);
    public abstract Future<Customer> getById(String id);
}
