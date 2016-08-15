package com.johnlewis.contactcentre.bff.customer.verticle.repository;

import com.johnlewis.contactcentre.bff.customer.domain.Customer;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerId;
import com.johnlewis.contactcentre.bff.customer.domain.CustomerSearchResults;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class FileSystemCustomerRepository extends CustomerRepository {

    @Getter
    private final List<Customer> customers;

    public FileSystemCustomerRepository() {
        this("data/customers.json");
    }

    @SneakyThrows(IOException.class)
    public FileSystemCustomerRepository(String dataSource) {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(dataSource);
        JsonObject customersJson = new JsonObject(IOUtils.toString(stream, "UTF-8"));

        JsonArray customersList = customersJson.getJsonArray("customers");

        customers = IntStream.range(0, customersList.size())
                .mapToObj(customersList::getJsonObject)
                .map(Customer::from)
                .collect(toList());

        System.out.println("Generated mock customer repository (size: "+customers.size()+")");
    }

    protected FileSystemCustomerRepository(List<Customer> customers) {
        this.customers = customers;
    }

    public Future<CustomerSearchResults> searchCustomers(String query) {
        List<Customer> matchingCustomers = customers.stream()
                .filter(customer -> matches(customer, query))
                .collect(toList());

        Future<CustomerSearchResults> future = Future.future();
        future.complete(new CustomerSearchResults(matchingCustomers));

        return future;
    }

    @Override
    public Future<Customer> getById(String id) {
        Customer matchingCustomer = customers.stream()
                .filter(customer -> customer.matches(id))
                .findFirst()
                .orElse(null);

        Future<Customer> future = Future.future();
        future.complete(matchingCustomer);

        return future;
    }

    private boolean matches(Customer customer, String query) {
        String lcQuery = query.toLowerCase();
        return customer.getName().toLowerCase().contains(lcQuery);
    }
}
